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

class FindNoiseCosmeticRendersUseCaseTest {

    private ImagesService imagesService;

    @BeforeEach
    void setUp() {
        imagesService = new ImagesService(
                new InMemoryNoiseBaseStructureRepository(),
                new InMemoryNoiseCosmeticRenderRepository());
    }

    @Test
    void shouldReturnAllRendersForGivenBase() throws IOException {
        NoiseCosmeticRender render1 = imagesService.rate(cmdWithColors(42, "#000000", 3));
        NoiseCosmeticRender render2 = imagesService.rate(cmdWithColors(42, "#FF0000", 5));

        UUID baseId = render1.baseStructureId();
        List<NoiseCosmeticRender> renders = imagesService.findRendersByBaseId(baseId);

        assertThat(renders).hasSize(2);
        assertThat(renders).extracting(NoiseCosmeticRender::id)
                .containsExactlyInAnyOrder(render1.id(), render2.id());
    }

    @Test
    void shouldReturnEmptyListWhenNoRendersExistForBase() {
        List<NoiseCosmeticRender> renders = imagesService.findRendersByBaseId(UUID.randomUUID());

        assertThat(renders).isEmpty();
    }

    @Test
    void shouldNotReturnRendersOfOtherBases() throws IOException {
        NoiseCosmeticRender renderBase1 = imagesService.rate(cmdWithColors(42, "#000000", 3));
        imagesService.rate(cmdWithColors(99, "#FF0000", 4));

        List<NoiseCosmeticRender> renders = imagesService.findRendersByBaseId(renderBase1.baseStructureId());

        assertThat(renders).hasSize(1);
        assertThat(renders.get(0).id()).isEqualTo(renderBase1.id());
    }

    private ImageRequestCmd cmdWithColors(int seed, String back, int note) {
        return ImageRequestCmd.builder()
                .note(note)
                .sizeCmd(ImageRequestCmd.SizeCmd.builder()
                        .width(10).height(10).seed(seed).octaves(2)
                        .persistence(0.5).lacunarity(2.0).scale(100.0).build())
                .colorCmd(ImageRequestCmd.ColorCmd.builder()
                        .back(back).middle("#888888").fore("#FFFFFF")
                        .backThreshold(0.4).middleThreshold(0.7).build())
                .build();
    }

    private static class InMemoryNoiseBaseStructureRepository implements NoiseBaseStructureRepository {
        private final List<NoiseBaseStructure> db = new ArrayList<>();

        @Override
        public NoiseBaseStructure save(NoiseBaseStructure structure) {
            db.removeIf(b -> b.id().equals(structure.id()));
            db.add(structure);
            return structure;
        }

        @Override
        public List<NoiseBaseStructure> findAll() { return List.copyOf(db); }

        @Override
        public Optional<NoiseBaseStructure> findByConfigHash(int hash) {
            return db.stream().filter(b -> b.configHash() == hash).findFirst();
        }

        @Override
        public void deleteById(UUID id) { db.removeIf(b -> b.id().equals(id)); }

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
        private final List<NoiseCosmeticRender> db = new ArrayList<>();

        @Override
        public NoiseCosmeticRender save(NoiseCosmeticRender render) {
            db.removeIf(r -> r.id().equals(render.id()));
            db.add(render);
            return render;
        }

        @Override
        public void deleteById(UUID id) { db.removeIf(r -> r.id().equals(id)); }

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