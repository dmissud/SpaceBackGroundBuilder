package org.dbs.sbgb.domain.factory;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.PerlinGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NoiseGeneratorFactory")
class NoiseGeneratorFactoryTest {

    private NoiseGeneratorFactory factory;

    @BeforeEach
    void setUp() {
        factory = new NoiseGeneratorFactory();
    }

    @Test
    @DisplayName("should create single-layer Perlin noise when multi-layer is disabled")
    void shouldCreateSingleLayerNoise() {
        // Given
        GalaxyParameters parameters = GalaxyParameters.createDefault();
        long seed = 12345L;
        int width = 1000;
        int height = 1000;

        // When
        PerlinGenerator result = factory.createNoiseGenerator(
                parameters,
                seed,
                width,
                height,
                Interpolation.COSINE,
                FadeFunction.CUBIC_POLY
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isNotInstanceOf(org.dbs.sbgb.domain.model.MultiLayerNoiseAdapter.class);
    }

    @Test
    @DisplayName("should create multi-layer noise when multi-layer is enabled")
    void shouldCreateMultiLayerNoise() {
        // Given
        GalaxyParameters parameters = GalaxyParameters.builder()
                .galaxyType(org.dbs.sbgb.domain.model.GalaxyType.SPIRAL)
                .numberOfArms(2)
                .armWidth(80.0)
                .armRotation(4.0)
                .coreSize(0.05)
                .galaxyRadius(1500.0)
                .noiseOctaves(4)
                .noisePersistence(0.5)
                .noiseLacunarity(2.0)
                .noiseScale(200.0)
                .multiLayerNoiseEnabled(true)
                .build();

        long seed = 12345L;
        int width = 1000;
        int height = 1000;

        // When
        PerlinGenerator result = factory.createNoiseGenerator(
                parameters,
                seed,
                width,
                height,
                Interpolation.COSINE,
                FadeFunction.CUBIC_POLY
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(org.dbs.sbgb.domain.model.MultiLayerNoiseAdapter.class);
    }

    @Test
    @DisplayName("should produce different noise values for different seeds")
    void shouldProduceDifferentNoiseValuesForDifferentSeeds() {
        // Given
        GalaxyParameters parameters = GalaxyParameters.builder()
                .galaxyType(org.dbs.sbgb.domain.model.GalaxyType.SPIRAL)
                .numberOfArms(2)
                .armWidth(80.0)
                .armRotation(4.0)
                .coreSize(0.05)
                .galaxyRadius(1500.0)
                .noiseOctaves(4)
                .noisePersistence(0.5)
                .noiseLacunarity(2.0)
                .noiseScale(0.5)  // Use scale that generates non-zero values
                .build();

        int width = 1000;
        int height = 1000;

        // When
        PerlinGenerator noise1 = factory.createNoiseGenerator(
                parameters, 111L, width, height, Interpolation.COSINE, FadeFunction.CUBIC_POLY);
        PerlinGenerator noise2 = factory.createNoiseGenerator(
                parameters, 222L, width, height, Interpolation.COSINE, FadeFunction.CUBIC_POLY);

        // Sample multiple points and check if at least one differs
        boolean foundDifference = false;
        for (int x = 100; x < width; x += 100) {
            for (int y = 100; y < height; y += 100) {
                double value1 = noise1.scaleNoiseNormalizedValue(x, y);
                double value2 = noise2.scaleNoiseNormalizedValue(x, y);
                if (Math.abs(value1 - value2) > 0.001) {
                    foundDifference = true;
                    break;
                }
            }
            if (foundDifference) break;
        }

        // Then
        assertThat(foundDifference)
                .as("Expected at least one noise value to differ between different seeds")
                .isTrue();
    }

    @Test
    @DisplayName("should produce reproducible noise for same seed")
    void shouldProduceReproducibleNoise() {
        // Given
        GalaxyParameters parameters = GalaxyParameters.builder()
                .galaxyType(org.dbs.sbgb.domain.model.GalaxyType.SPIRAL)
                .numberOfArms(2)
                .armWidth(80.0)
                .armRotation(4.0)
                .coreSize(0.05)
                .galaxyRadius(1500.0)
                .noiseOctaves(4)
                .noisePersistence(0.5)
                .noiseLacunarity(2.0)
                .noiseScale(0.5)
                .build();

        int width = 100;
        int height = 100;
        long seed = 12345L;

        // When
        PerlinGenerator noise1 = factory.createNoiseGenerator(
                parameters, seed, width, height, Interpolation.COSINE, FadeFunction.CUBIC_POLY);
        PerlinGenerator noise2 = factory.createNoiseGenerator(
                parameters, seed, width, height, Interpolation.COSINE, FadeFunction.CUBIC_POLY);

        double value1 = noise1.scaleNoiseNormalizedValue(50, 50);
        double value2 = noise2.scaleNoiseNormalizedValue(50, 50);

        // Then
        assertThat(value1).isEqualTo(value2);
    }

    @Test
    @DisplayName("should return normalized values between 0 and 1")
    void shouldReturnNormalizedValues() {
        // Given
        GalaxyParameters parameters = GalaxyParameters.builder()
                .galaxyType(org.dbs.sbgb.domain.model.GalaxyType.SPIRAL)
                .numberOfArms(2)
                .armWidth(80.0)
                .armRotation(4.0)
                .coreSize(0.05)
                .galaxyRadius(1500.0)
                .noiseOctaves(4)
                .noisePersistence(0.5)
                .noiseLacunarity(2.0)
                .noiseScale(0.5)
                .build();

        int width = 100;
        int height = 100;

        PerlinGenerator noise = factory.createNoiseGenerator(
                parameters, 12345L, width, height, Interpolation.COSINE, FadeFunction.CUBIC_POLY);

        // When / Then
        for (int x = 0; x < width; x += 10) {
            for (int y = 0; y < height; y += 10) {
                double value = noise.scaleNoiseNormalizedValue(x, y);
                assertThat(value)
                        .as("Noise value at (%d, %d) should be between 0 and 1", x, y)
                        .isBetween(0.0, 1.0);
            }
        }
    }
}
