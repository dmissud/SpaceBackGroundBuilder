package org.dbs.sbgb.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NoiseBaseStructureTest {

    @Test
    void shouldProduceSameConfigHashForIdenticalParams() {
        NoiseBaseStructure base1 = buildBase(UUID.randomUUID(), 1920, 1080, 42, 4, 0.5, 2.0, 100.0, "FBM", false, null);
        NoiseBaseStructure base2 = buildBase(UUID.randomUUID(), 1920, 1080, 42, 4, 0.5, 2.0, 100.0, "FBM", false, null);

        assertThat(base1.configHash()).isEqualTo(base2.configHash());
    }

    @Test
    void shouldProduceDifferentConfigHashForDifferentSeed() {
        NoiseBaseStructure base1 = buildBase(UUID.randomUUID(), 1920, 1080, 42, 4, 0.5, 2.0, 100.0, "FBM", false, null);
        NoiseBaseStructure base2 = buildBase(UUID.randomUUID(), 1920, 1080, 99, 4, 0.5, 2.0, 100.0, "FBM", false, null);

        assertThat(base1.configHash()).isNotEqualTo(base2.configHash());
    }

    @Test
    void shouldProduceStableConfigHash() {
        NoiseBaseStructure base = buildBase(UUID.randomUUID(), 1920, 1080, 42, 4, 0.5, 2.0, 100.0, "FBM", false, null);

        assertThat(base.configHash()).isEqualTo(base.configHash());
    }

    @Test
    void shouldIgnoreIdAndMaxNoteInConfigHash() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        NoiseBaseStructure base1 = new NoiseBaseStructure(id1, "desc", 5, 1920, 1080, 42, 4, 0.5, 2.0, 100.0, "FBM", false, null);
        NoiseBaseStructure base2 = new NoiseBaseStructure(id2, "other desc", 0, 1920, 1080, 42, 4, 0.5, 2.0, 100.0, "FBM", false, null);

        assertThat(base1.configHash()).isEqualTo(base2.configHash());
    }

    @Test
    void shouldGenerateNonNullDescription() {
        NoiseBaseStructure base = buildBase(UUID.randomUUID(), 1920, 1080, 42, 4, 0.5, 2.0, 100.0, "FBM", false, null);

        assertThat(base.generateDescription()).isNotNull().isNotBlank();
    }

    private NoiseBaseStructure buildBase(UUID id, int width, int height, int seed, int octaves,
                                         double persistence, double lacunarity, double scale,
                                         String noiseType, boolean useMultiLayer, String layersConfig) {
        return new NoiseBaseStructure(id, "description", 0, width, height, seed, octaves,
                persistence, lacunarity, scale, noiseType, useMultiLayer, layersConfig);
    }
}
