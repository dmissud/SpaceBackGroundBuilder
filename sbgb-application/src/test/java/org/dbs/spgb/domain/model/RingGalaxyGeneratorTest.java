package org.dbs.spgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RingGalaxyGeneratorTest {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    private static final long SEED = 42L;
    private static final double GALAXY_RADIUS = 200.0;
    private static final double CORE_SIZE = 0.05;
    private static final double RING_RADIUS = 120.0;
    private static final double RING_WIDTH = 30.0;
    private static final double RING_INTENSITY = 1.0;
    private static final double CORE_TO_RING_RATIO = 0.3;

    private RingGalaxyGenerator generator;

    @BeforeEach
    void setUp() {
        PerlinGenerator noiseGenerator = new PerlinGenerator(Interpolation.LINEAR, FadeFunction.NONE);
        noiseGenerator.createNoisePipeline(SEED, WIDTH, HEIGHT, 4, 0.5, 2.0, 200.0, NoiseType.FBM);
        noiseGenerator.performNormalization();

        generator = RingGalaxyGenerator.builder()
                .width(WIDTH)
                .height(HEIGHT)
                .noiseGenerator(noiseGenerator)
                .coreSize(CORE_SIZE)
                .galaxyRadius(GALAXY_RADIUS)
                .ringRadius(RING_RADIUS)
                .ringWidth(RING_WIDTH)
                .ringIntensity(RING_INTENSITY)
                .coreToRingRatio(CORE_TO_RING_RATIO)
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

        assertThat(intensity).isGreaterThan(0.1);
    }

    @Test
    void shouldReturnHighIntensityOnRing() {
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;

        // Point on the ring
        int ringX = centerX + (int) RING_RADIUS;
        int ringY = centerY;

        double ringIntensity = generator.calculateGalaxyIntensity(ringX, ringY);

        // Point between core and ring
        int betweenX = centerX + (int) (RING_RADIUS * 0.5);
        int betweenY = centerY;
        double betweenIntensity = generator.calculateGalaxyIntensity(betweenX, betweenY);

        // Ring should be brighter than the gap between core and ring
        assertThat(ringIntensity).isGreaterThan(betweenIntensity);
    }

    @Test
    void shouldBeReproducibleWithSameSeed() {
        PerlinGenerator noiseGenerator2 = new PerlinGenerator(Interpolation.LINEAR, FadeFunction.NONE);
        noiseGenerator2.createNoisePipeline(SEED, WIDTH, HEIGHT, 4, 0.5, 2.0, 200.0, NoiseType.FBM);
        noiseGenerator2.performNormalization();

        RingGalaxyGenerator generator2 = RingGalaxyGenerator.builder()
                .width(WIDTH)
                .height(HEIGHT)
                .noiseGenerator(noiseGenerator2)
                .coreSize(CORE_SIZE)
                .galaxyRadius(GALAXY_RADIUS)
                .ringRadius(RING_RADIUS)
                .ringWidth(RING_WIDTH)
                .ringIntensity(RING_INTENSITY)
                .coreToRingRatio(CORE_TO_RING_RATIO)
                .build();

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
