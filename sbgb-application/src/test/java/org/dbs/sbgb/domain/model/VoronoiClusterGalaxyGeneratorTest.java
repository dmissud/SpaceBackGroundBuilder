package org.dbs.sbgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import org.dbs.sbgb.domain.model.parameters.CoreParameters;
import org.dbs.sbgb.domain.model.parameters.VoronoiClusterParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VoronoiClusterGalaxyGeneratorTest {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    private static final long SEED = 42L;
    private static final double GALAXY_RADIUS = 200.0;
    private static final double CORE_SIZE = 0.05;
    private static final int CLUSTER_COUNT = 80;
    private static final double CLUSTER_SIZE = 60.0;
    private static final double CLUSTER_CONCENTRATION = 0.7;

    private VoronoiClusterGalaxyGenerator generator;

    @BeforeEach
    void setUp() {
        PerlinGenerator noiseGenerator = new PerlinGenerator(Interpolation.LINEAR, FadeFunction.NONE);
        noiseGenerator.createNoisePipeline(SEED, WIDTH, HEIGHT, 4, 0.5, 2.0, 200.0, NoiseType.FBM);
        noiseGenerator.performNormalization();

        generator = VoronoiClusterGalaxyGenerator.builder()
                .width(WIDTH)
                .height(HEIGHT)
                .noiseGenerator(noiseGenerator)
                .seed(SEED)
                .coreParameters(CoreParameters.builder()
                        .coreSize(CORE_SIZE)
                        .galaxyRadius(GALAXY_RADIUS)
                        .build())
                .voronoiParameters(VoronoiClusterParameters.builder()
                        .clusterCount(CLUSTER_COUNT)
                        .clusterSize(CLUSTER_SIZE)
                        .clusterConcentration(CLUSTER_CONCENTRATION)
                        .build())
                .build();
    }

    @Test
    void shouldReturnZeroOutsideGalaxyRadius() {
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;
        // Point well outside galaxy radius
        int farX = centerX + (int) (GALAXY_RADIUS * 1.5);
        int farY = centerY;

        double intensity = generator.calculateGalaxyIntensity(farX, farY);

        assertThat(intensity).isEqualTo(0.0);
    }

    @Test
    void shouldReturnHighIntensityNearCore() {
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;

        double intensity = generator.calculateGalaxyIntensity(centerX, centerY);

        assertThat(intensity).isGreaterThan(0.5);
    }

    @Test
    void shouldReturnPositiveIntensityNearCluster() {
        // Generate with known seed and check that at least some pixels
        // within the galaxy radius have positive intensity from clusters
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;
        boolean foundPositive = false;

        // Sample points within the galaxy radius
        for (int angle = 0; angle < 360; angle += 10) {
            double rad = Math.toRadians(angle);
            int x = centerX + (int) (GALAXY_RADIUS * 0.5 * Math.cos(rad));
            int y = centerY + (int) (GALAXY_RADIUS * 0.5 * Math.sin(rad));
            double intensity = generator.calculateGalaxyIntensity(x, y);
            if (intensity > 0.0) {
                foundPositive = true;
                break;
            }
        }

        assertThat(foundPositive).isTrue();
    }

    @Test
    void shouldBeReproducibleWithSameSeed() {
        PerlinGenerator noiseGenerator2 = new PerlinGenerator(Interpolation.LINEAR, FadeFunction.NONE);
        noiseGenerator2.createNoisePipeline(SEED, WIDTH, HEIGHT, 4, 0.5, 2.0, 200.0, NoiseType.FBM);
        noiseGenerator2.performNormalization();

        VoronoiClusterGalaxyGenerator generator2 = VoronoiClusterGalaxyGenerator.builder()
                .width(WIDTH)
                .height(HEIGHT)
                .noiseGenerator(noiseGenerator2)
                .seed(SEED)
                .coreParameters(CoreParameters.builder()
                        .coreSize(CORE_SIZE)
                        .galaxyRadius(GALAXY_RADIUS)
                        .build())
                .voronoiParameters(VoronoiClusterParameters.builder()
                        .clusterCount(CLUSTER_COUNT)
                        .clusterSize(CLUSTER_SIZE)
                        .clusterConcentration(CLUSTER_CONCENTRATION)
                        .build())
                .build();

        // Check multiple points
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;

        assertThat(generator.calculateGalaxyIntensity(centerX, centerY))
                .isEqualTo(generator2.calculateGalaxyIntensity(centerX, centerY));
        assertThat(generator.calculateGalaxyIntensity(centerX + 50, centerY + 30))
                .isEqualTo(generator2.calculateGalaxyIntensity(centerX + 50, centerY + 30));
        assertThat(generator.calculateGalaxyIntensity(centerX - 80, centerY - 60))
                .isEqualTo(generator2.calculateGalaxyIntensity(centerX - 80, centerY - 60));
    }

    @Test
    void shouldReturnValuesBetweenZeroAndOne() {
        for (int x = 0; x < WIDTH; x += 10) {
            for (int y = 0; y < HEIGHT; y += 10) {
                double intensity = generator.calculateGalaxyIntensity(x, y);
                assertThat(intensity).isBetween(0.0, 1.0);
            }
        }
    }
}
