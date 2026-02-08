package org.dbs.sbgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EllipticalGalaxyGeneratorTest {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    private static final long SEED = 42L;
    private static final double GALAXY_RADIUS = 200.0;
    private static final double CORE_SIZE = 0.05;
    private static final double SERSIC_INDEX = 4.0;
    private static final double AXIS_RATIO = 0.7;
    private static final double ORIENTATION_ANGLE = 45.0;

    private EllipticalGalaxyGenerator generator;

    @BeforeEach
    void setUp() {
        PerlinGenerator noiseGenerator = new PerlinGenerator(Interpolation.LINEAR, FadeFunction.NONE);
        noiseGenerator.createNoisePipeline(SEED, WIDTH, HEIGHT, 4, 0.5, 2.0, 200.0, NoiseType.FBM);
        noiseGenerator.performNormalization();

        generator = EllipticalGalaxyGenerator.builder()
                .width(WIDTH)
                .height(HEIGHT)
                .noiseGenerator(noiseGenerator)
                .coreSize(CORE_SIZE)
                .galaxyRadius(GALAXY_RADIUS)
                .sersicIndex(SERSIC_INDEX)
                .axisRatio(AXIS_RATIO)
                .orientationAngle(ORIENTATION_ANGLE)
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
    void shouldReturnHigherIntensityAlongMajorAxis() {
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;
        double halfRadius = GALAXY_RADIUS * 0.4;

        // Point along major axis (orientation angle direction)
        double angleRad = Math.toRadians(ORIENTATION_ANGLE);
        int majorX = centerX + (int) (halfRadius * Math.cos(angleRad));
        int majorY = centerY + (int) (halfRadius * Math.sin(angleRad));

        // Point along minor axis (perpendicular)
        double minorAngleRad = Math.toRadians(ORIENTATION_ANGLE + 90.0);
        int minorX = centerX + (int) (halfRadius * Math.cos(minorAngleRad));
        int minorY = centerY + (int) (halfRadius * Math.sin(minorAngleRad));

        double majorIntensity = generator.calculateGalaxyIntensity(majorX, majorY);
        double minorIntensity = generator.calculateGalaxyIntensity(minorX, minorY);

        assertThat(majorIntensity).isGreaterThan(minorIntensity);
    }

    @Test
    void shouldBeReproducibleWithSameSeed() {
        PerlinGenerator noiseGenerator2 = new PerlinGenerator(Interpolation.LINEAR, FadeFunction.NONE);
        noiseGenerator2.createNoisePipeline(SEED, WIDTH, HEIGHT, 4, 0.5, 2.0, 200.0, NoiseType.FBM);
        noiseGenerator2.performNormalization();

        EllipticalGalaxyGenerator generator2 = EllipticalGalaxyGenerator.builder()
                .width(WIDTH)
                .height(HEIGHT)
                .noiseGenerator(noiseGenerator2)
                .coreSize(CORE_SIZE)
                .galaxyRadius(GALAXY_RADIUS)
                .sersicIndex(SERSIC_INDEX)
                .axisRatio(AXIS_RATIO)
                .orientationAngle(ORIENTATION_ANGLE)
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
