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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RateNoiseCosmeticRenderUseCaseTest {

    private List<NoiseBaseStructure> baseDb;
    private List<NoiseCosmeticRender> renderDb;
    private NoiseBaseStructureRepository baseRepo;
    private NoiseCosmeticRenderRepository renderRepo;
    private ImagesService imagesService;

    @BeforeEach
    void setUp() {
        baseDb = new ArrayList<>();
        renderDb = new ArrayList<>();
        baseRepo = new InMemoryNoiseBaseStructureRepository(baseDb);
        renderRepo = new InMemoryNoiseCosmeticRenderRepository(renderDb);
        imagesService = new ImagesService(baseRepo, renderRepo);
    }

    @Test
    void shouldInsertNewBaseAndNewRenderWhenNothingExists() throws IOException {
        ImageRequestCmd cmd = buildCmd(3);

        NoiseCosmeticRender result = imagesService.rate(cmd);

        assertThat(baseDb).hasSize(1);
        assertThat(renderDb).hasSize(1);
        assertThat(result.note()).isEqualTo(3);
        assertThat(baseDb.get(0).maxNote()).isEqualTo(3);
    }

    @Test
    void shouldReuseExistingBaseAndInsertNewRenderForDifferentColors() throws IOException {
        imagesService.rate(buildCmd("#000000", "#888888", "#FFFFFF", 3));

        ImageRequestCmd cmd2 = buildCmd("#FF0000", "#888888", "#FFFFFF", 5);
        NoiseCosmeticRender result = imagesService.rate(cmd2);

        assertThat(baseDb).hasSize(1);
        assertThat(renderDb).hasSize(2);
        assertThat(result.note()).isEqualTo(5);
        assertThat(baseDb.get(0).maxNote()).isEqualTo(5);
    }

    @Test
    void shouldUpdateNoteForExistingRender() throws IOException {
        imagesService.rate(buildCmd("#000000", "#888888", "#FFFFFF", 4));

        NoiseCosmeticRender result = imagesService.rate(buildCmd("#000000", "#888888", "#FFFFFF", 2));

        assertThat(baseDb).hasSize(1);
        assertThat(renderDb).hasSize(1);
        assertThat(result.note()).isEqualTo(2);
        assertThat(baseDb.get(0).maxNote()).isEqualTo(2);
    }

    @Test
    void shouldThrowExceptionForNoteOutOfRange() {
        ImageRequestCmd cmd = buildCmd(0);

        assertThatThrownBy(() -> imagesService.rate(cmd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Note must be between 1 and 5");
    }

    @Test
    void shouldThrowExceptionForNoteTooHigh() {
        ImageRequestCmd cmd = buildCmd(6);

        assertThatThrownBy(() -> imagesService.rate(cmd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Note must be between 1 and 5");
    }

    private ImageRequestCmd buildCmd(int note) {
        return buildCmd("#000000", "#888888", "#FFFFFF", note);
    }

    private ImageRequestCmd buildCmd(String back, String middle, String fore, int note) {
        return ImageRequestCmd.builder()
                .note(note)
                .sizeCmd(ImageRequestCmd.SizeCmd.builder()
                        .width(100).height(100).seed(42).octaves(2)
                        .persistence(0.5).lacunarity(2.0).scale(100.0).build())
                .colorCmd(ImageRequestCmd.ColorCmd.builder()
                        .back(back).middle(middle).fore(fore)
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
