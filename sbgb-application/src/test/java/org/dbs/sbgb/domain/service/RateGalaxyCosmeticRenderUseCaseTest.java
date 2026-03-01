package org.dbs.sbgb.domain.service;

import org.dbs.sbgb.domain.mapper.GalaxyStructureMapper;
import org.dbs.sbgb.domain.model.*;
import org.dbs.sbgb.domain.model.GalaxyBaseStructure;
import org.dbs.sbgb.domain.model.GalaxyCosmeticRender;
import org.dbs.sbgb.domain.strategy.GalaxyGeneratorFactory;
import org.dbs.sbgb.port.in.GalaxyRequestCmd;
import org.dbs.sbgb.port.in.BloomParameters;
import org.dbs.sbgb.port.in.ColorParameters;
import org.dbs.sbgb.port.in.NoiseParameters;
import org.dbs.sbgb.port.in.StarFieldParameters;
import org.dbs.sbgb.port.in.MultiLayerNoiseParameters;
import org.dbs.sbgb.port.in.SpiralParameters;
import org.dbs.sbgb.port.out.GalaxyBaseStructureRepository;
import org.dbs.sbgb.port.out.GalaxyCosmeticRenderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RateGalaxyCosmeticRenderUseCaseTest {

    @Mock private ImageSerializer imageSerializer;
    @Mock private org.dbs.sbgb.port.out.GalaxyImageComputationPort galaxyImageComputationPort;

    private List<GalaxyBaseStructure> baseDb;
    private List<GalaxyCosmeticRender> renderDb;
    private GalaxyService galaxyService;

    @BeforeEach
    void setUp() throws IOException {
        org.mockito.MockitoAnnotations.openMocks(this);
        baseDb = new ArrayList<>();
        renderDb = new ArrayList<>();

        GalaxyBaseStructureRepository baseRepo = new InMemoryGalaxyBaseStructureRepository(baseDb);
        GalaxyCosmeticRenderRepository renderRepo = new InMemoryGalaxyCosmeticRenderRepository(renderDb);

        galaxyService = new GalaxyService(
                baseRepo, renderRepo,
                imageSerializer,
                galaxyImageComputationPort);

        when(galaxyImageComputationPort.computeImage(anyInt(), any())).thenReturn(new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB));
        when(imageSerializer.toByteArray(any(BufferedImage.class))).thenReturn(new byte[]{1, 2, 3});
    }

    @Test
    void shouldInsertNewBaseAndNewRenderWhenNothingExists() throws IOException {
        GalaxyCosmeticRender result = galaxyService.rate(buildCmd(3));

        assertThat(baseDb).hasSize(1);
        assertThat(renderDb).hasSize(1);
        assertThat(result.note()).isEqualTo(3);
        assertThat(baseDb.get(0).maxNote()).isEqualTo(3);
    }

    @Test
    void shouldReuseExistingBaseAndInsertNewRenderForDifferentCosmetics() throws IOException {
        galaxyService.rate(buildCmd("#CLASSIC", 3));

        GalaxyCosmeticRender result = galaxyService.rate(buildCmd("#NEBULA", 5));

        assertThat(baseDb).hasSize(1);
        assertThat(renderDb).hasSize(2);
        assertThat(result.note()).isEqualTo(5);
        assertThat(baseDb.get(0).maxNote()).isEqualTo(5);
    }

    @Test
    void shouldUpdateNoteForExistingRenderWithSameCosmetics() throws IOException {
        galaxyService.rate(buildCmd(4));

        GalaxyCosmeticRender result = galaxyService.rate(buildCmd(2));

        assertThat(baseDb).hasSize(1);
        assertThat(renderDb).hasSize(1);
        assertThat(result.note()).isEqualTo(2);
        assertThat(baseDb.get(0).maxNote()).isEqualTo(2);
    }

    @Test
    void shouldRejectNoteOutOfRange() {
        assertThatThrownBy(() -> galaxyService.rate(buildCmd(0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Note must be between 1 and 5");
    }

    @Test
    void shouldRejectNoteTooHigh() {
        assertThatThrownBy(() -> galaxyService.rate(buildCmd(6)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Note must be between 1 and 5");
    }

    @Test
    void shouldStoreSpiralParamsInBase() throws IOException {
        galaxyService.rate(buildCmd(3));

        GalaxyBaseStructure savedBase = baseDb.get(0);
        assertThat(savedBase.numberOfArms()).isEqualTo(2);
        assertThat(savedBase.armWidth()).isEqualTo(80.0);
        assertThat(savedBase.armRotation()).isEqualTo(4.0);
        assertThat(savedBase.darkLaneOpacity()).isEqualTo(0.0);
    }

    private GalaxyRequestCmd buildCmd(int note) {
        return buildCmd("#CLASSIC", note);
    }

    private GalaxyRequestCmd buildCmd(String palette, int note) {
        return GalaxyRequestCmd.builder()
                .note(note)
                .width(100).height(100).seed(42L)
                .galaxyType("SPIRAL")
                .coreSize(0.05).galaxyRadius(200.0).warpStrength(0.0)
                .noiseParameters(NoiseParameters.defaultNoise())
                .spiralParameters(SpiralParameters.builder()
                        .numberOfArms(2).armWidth(80.0).armRotation(4.0).darkLaneOpacity(0.0).build())
                .starFieldParameters(StarFieldParameters.noStars())
                .multiLayerNoiseParameters(MultiLayerNoiseParameters.disabled())
                .bloomParameters(BloomParameters.disabled())
                .colorParameters(ColorParameters.builder().colorPalette(palette).build())
                .build();
    }

    // ========== In-memory repositories ==========

    private static class InMemoryGalaxyBaseStructureRepository implements GalaxyBaseStructureRepository {
        private final List<GalaxyBaseStructure> db;

        InMemoryGalaxyBaseStructureRepository(List<GalaxyBaseStructure> db) {
            this.db = db;
        }

        @Override
        public GalaxyBaseStructure save(GalaxyBaseStructure base) {
            db.removeIf(b -> b.id().equals(base.id()));
            db.add(base);
            return base;
        }

        @Override
        public List<GalaxyBaseStructure> findAll() {
            return List.copyOf(db);
        }

        @Override
        public Optional<GalaxyBaseStructure> findById(UUID id) {
            return db.stream().filter(b -> b.id().equals(id)).findFirst();
        }

        @Override
        public Optional<GalaxyBaseStructure> findByConfigHash(int hash) {
            return db.stream().filter(b -> b.configHash() == hash).findFirst();
        }

        @Override
        public void deleteById(UUID id) {
            db.removeIf(b -> b.id().equals(id));
        }

        @Override
        public GalaxyBaseStructure updateMaxNote(UUID id, int maxNote) {
            GalaxyBaseStructure e = db.stream().filter(b -> b.id().equals(id)).findFirst().orElseThrow();
            GalaxyBaseStructure updated = new GalaxyBaseStructure(e.id(), e.description(), maxNote,
                    e.width(), e.height(), e.seed(), e.galaxyType(), e.coreSize(), e.galaxyRadius(),
                    e.warpStrength(), e.noiseOctaves(), e.noisePersistence(), e.noiseLacunarity(), e.noiseScale(),
                    e.multiLayerEnabled(), e.macroLayerScale(), e.macroLayerWeight(),
                    e.mesoLayerScale(), e.mesoLayerWeight(), e.microLayerScale(), e.microLayerWeight(),
                    e.structureParams(),
                    e.numberOfArms(), e.armWidth(), e.armRotation(), e.darkLaneOpacity(),
                    e.clusterCount(), e.clusterSize(), e.clusterConcentration(),
                    e.sersicIndex(), e.axisRatio(), e.orientationAngle(),
                    e.ringRadius(), e.ringWidth(), e.ringIntensity(), e.coreToRingRatio(),
                    e.irregularity(), e.irregularClumpCount(), e.irregularClumpSize());
            db.removeIf(b -> b.id().equals(id));
            db.add(updated);
            return updated;
        }
    }

    private static class InMemoryGalaxyCosmeticRenderRepository implements GalaxyCosmeticRenderRepository {
        private final List<GalaxyCosmeticRender> db;

        InMemoryGalaxyCosmeticRenderRepository(List<GalaxyCosmeticRender> db) {
            this.db = db;
        }

        @Override
        public GalaxyCosmeticRender save(GalaxyCosmeticRender render) {
            db.removeIf(r -> r.id().equals(render.id()));
            db.add(render);
            return render;
        }

        @Override
        public void deleteById(UUID id) {
            db.removeIf(r -> r.id().equals(id));
        }

        @Override
        public Optional<GalaxyCosmeticRender> findById(UUID id) {
            return db.stream().filter(r -> r.id().equals(id)).findFirst();
        }

        @Override
        public Optional<GalaxyCosmeticRender> findByBaseStructureIdAndCosmeticHash(UUID baseId, int cosmeticHash) {
            return db.stream()
                    .filter(r -> r.baseStructureId().equals(baseId) && r.cosmeticHash() == cosmeticHash)
                    .findFirst();
        }

        @Override
        public List<GalaxyCosmeticRender> findAllByBaseStructureId(UUID baseId) {
            return db.stream().filter(r -> r.baseStructureId().equals(baseId)).toList();
        }
    }
}
