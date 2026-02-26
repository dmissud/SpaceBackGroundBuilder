package org.dbs.sbgb.domain.model;

import org.dbs.sbgb.domain.model.parameters.CoreParameters;
import org.dbs.sbgb.domain.model.parameters.LenticularShapeParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LenticularGalaxyGeneratorTest {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    private static final long SEED = 42L;
    private static final double GALAXY_RADIUS = 200.0;
    private static final double CORE_SIZE = 0.05;

    private LenticularGalaxyGenerator generator;

    @BeforeEach
    void setUp() {
        generator = LenticularGalaxyGenerator.builder()
                .width(WIDTH)
                .height(HEIGHT)
                .seed(SEED)
                .coreParameters(CoreParameters.builder()
                        .coreSize(CORE_SIZE)
                        .galaxyRadius(GALAXY_RADIUS)
                        .build())
                .lenticularParameters(LenticularShapeParameters.builder()
                        .sersicIndex(2.5)
                        .axisRatio(0.6)
                        .orientationAngle(0.0)
                        .diskContribution(0.4)
                        .build())
                .build();
    }

    @Test
    void shouldReturnZeroOutsideGalaxyRadius() {
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;
        int farX = centerX + (int) (GALAXY_RADIUS * 1.5);

        double intensity = generator.calculateGalaxyIntensity(farX, centerY);

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
    void shouldReturnValuesBetweenZeroAndOne() {
        for (int x = 0; x < WIDTH; x += 10) {
            for (int y = 0; y < HEIGHT; y += 10) {
                double intensity = generator.calculateGalaxyIntensity(x, y);
                assertThat(intensity).isBetween(0.0, 1.0);
            }
        }
    }

    @Test
    void shouldBeReproducibleWithSameSeed() {
        LenticularGalaxyGenerator generator2 = LenticularGalaxyGenerator.builder()
                .width(WIDTH)
                .height(HEIGHT)
                .seed(SEED)
                .coreParameters(CoreParameters.builder()
                        .coreSize(CORE_SIZE)
                        .galaxyRadius(GALAXY_RADIUS)
                        .build())
                .lenticularParameters(LenticularShapeParameters.builder()
                        .sersicIndex(2.5)
                        .axisRatio(0.6)
                        .orientationAngle(0.0)
                        .diskContribution(0.4)
                        .build())
                .build();

        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;

        assertThat(generator.calculateGalaxyIntensity(centerX, centerY))
                .isEqualTo(generator2.calculateGalaxyIntensity(centerX, centerY));
        assertThat(generator.calculateGalaxyIntensity(centerX + 50, centerY + 30))
                .isEqualTo(generator2.calculateGalaxyIntensity(centerX + 50, centerY + 30));
    }

}
