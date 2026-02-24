package org.dbs.sbgb.domain.service;

import org.dbs.sbgb.domain.model.NoiseBaseStructure;
import org.dbs.sbgb.domain.model.NoiseCosmeticRender;
import org.dbs.sbgb.port.in.ImageRequestCmd;
import org.dbs.sbgb.port.out.NoiseBaseStructureRepository;
import org.dbs.sbgb.port.out.NoiseCosmeticRenderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class FindAndDeleteNoiseCosmeticRenderUseCaseTest {

    private List<NoiseBaseStructure> baseDb;
    private List<NoiseCosmeticRender> renderDb;
    private ImagesService imagesService;

    @BeforeEach
    void setUp() {
        baseDb = new ArrayList<>();
        renderDb = new ArrayList<>();
        imagesService = new ImagesService(
                new InMemoryNoiseBaseStructureRepository(baseDb),
                new InMemoryNoiseCosmeticRenderRepository(renderDb));
    }

    @Test
    void shouldReturnBasesSortedByMaxNoteDesc() throws IOException {
        imagesService.rate(buildCmd(1920, 1080, 42, "#000000", 3));
        imagesService.rate(buildCmd(800, 600, 99, "#FF0000", 5));
        imagesService.rate(buildCmd(1024, 768, 77, "#00FF00", 1));

        List<NoiseBaseStructure> bases = imagesService.findAllSortedByMaxNoteDesc();

        assertThat(bases).hasSize(3);
        assertThat(bases.get(0).maxNote()).isGreaterThanOrEqualTo(bases.get(1).maxNote());
        assertThat(bases.get(1).maxNote()).isGreaterThanOrEqualTo(bases.get(2).maxNote());
    }

    @Test
    void shouldDeleteBaseWhenLastRenderIsDeleted() throws IOException {
        NoiseCosmeticRender render = imagesService.rate(buildCmd(1920, 1080, 42, "#000000", 3));

        imagesService.deleteRender(render.id());

        assertThat(renderDb).isEmpty();
        assertThat(baseDb).isEmpty();
    }

    @Test
    void shouldKeepBaseAndRecalculateMaxNoteAfterDeletingOneRenderOfMany() throws IOException {
        imagesService.rate(buildCmd(1920, 1080, 42, "#000000", 5));
        NoiseCosmeticRender render2 = imagesService.rate(buildCmd(1920, 1080, 42, "#FF0000", 2));

        imagesService.deleteRender(render2.id());

        assertThat(renderDb).hasSize(1);
        assertThat(baseDb).hasSize(1);
        assertThat(baseDb.get(0).maxNote()).isEqualTo(5);
    }

    private ImageRequestCmd buildCmd(int width, int height, int seed, String back, int note) {
        return ImageRequestCmd.builder()
                .note(note)
                .sizeCmd(ImageRequestCmd.SizeCmd.builder()
                        .width(width).height(height).seed(seed).octaves(2)
                        .persistence(0.5).lacunarity(2.0).scale(100.0).build())
                .colorCmd(ImageRequestCmd.ColorCmd.builder()
                        .back(back).middle("#888888").fore("#FFFFFF")
                        .backThreshold(0.4).middleThreshold(0.7).build())
                .build();
    }

    private static class InMemoryNoiseBaseStructureRepository implements NoiseBaseStructureRepository {
        private final List<NoiseBaseStructure> db;

        InMemoryNoiseBaseStructureRepository(List<NoiseBaseStructure> db) {
            this.db = db;
        }

        @Override
        public NoiseBaseStructure save(NoiseBaseStructure structure) {
            db.removeIf(b -> b.id().equals(structure.id()));
            db.add(structure);
            return structure;
        }

        @Override
        public List<NoiseBaseStructure> findAll() {
            return List.copyOf(db);
        }

        @Override
        public Optional<NoiseBaseStructure> findByConfigHash(int hash) {
            return db.stream().filter(b -> b.configHash() == hash).findFirst();
        }

        @Override
        public void deleteById(UUID id) {
            db.removeIf(b -> b.id().equals(id));
        }

        @Override
        public NoiseBaseStructure updateMaxNote(UUID id, int maxNote) {
            NoiseBaseStructure existing = db.stream().filter(b -> b.id().equals(id)).findFirst().orElseThrow();
            NoiseBaseStructure updated = new NoiseBaseStructure(existing.id(), existing.description(), maxNote,
                    existing.width(), existing.height(), existing.seed(), existing.octaves(),
                    existing.persistence(), existing.lacunarity(), existing.scale(),
                    existing.noiseType(), existing.useMultiLayer(), existing.layersConfig());
            db.removeIf(b -> b.id().equals(id));
            db.add(updated);
            return updated;
        }
    }

    private static class InMemoryNoiseCosmeticRenderRepository implements NoiseCosmeticRenderRepository {
        private final List<NoiseCosmeticRender> db;

        InMemoryNoiseCosmeticRenderRepository(List<NoiseCosmeticRender> db) {
            this.db = db;
        }

        @Override
        public NoiseCosmeticRender save(NoiseCosmeticRender render) {
            db.removeIf(r -> r.id().equals(render.id()));
            db.add(render);
            return render;
        }

        @Override
        public void deleteById(UUID id) {
            db.removeIf(r -> r.id().equals(id));
        }

        @Override
        public Optional<NoiseCosmeticRender> findById(UUID id) {
            return db.stream().filter(r -> r.id().equals(id)).findFirst();
        }

        @Override
        public Optional<NoiseCosmeticRender> findByBaseStructureIdAndCosmeticHash(UUID baseStructureId, int cosmeticHash) {
            return db.stream()
                    .filter(r -> r.baseStructureId().equals(baseStructureId) && r.cosmeticHash() == cosmeticHash)
                    .findFirst();
        }

        @Override
        public List<NoiseCosmeticRender> findAllByBaseStructureId(UUID baseStructureId) {
            return db.stream().filter(r -> r.baseStructureId().equals(baseStructureId)).toList();
        }
    }
}
