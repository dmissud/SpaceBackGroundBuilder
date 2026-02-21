package org.dbs.sbgb.domain.model;

import org.dbs.sbgb.domain.model.parameters.CoreParameters;
import org.dbs.sbgb.domain.model.parameters.SpiralStructureParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpiralGalaxyGeneratorTest {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    private static final long SEED = 42L;
    private static final double GALAXY_RADIUS = 200.0;
    private static final double CORE_SIZE = 0.05;
    private static final int NUM_ARMS = 2;
    private static final double ARM_WIDTH = 40.0;
    private static final double ARM_ROTATION = 4.0;

    private SpiralGalaxyGenerator generator;

    @BeforeEach
    void setUp() {
        generator = SpiralGalaxyGenerator.builder()
                .width(WIDTH)
                .height(HEIGHT)
                .seed(SEED)
                .coreParameters(CoreParameters.builder()
                        .coreSize(CORE_SIZE)
                        .galaxyRadius(GALAXY_RADIUS)
                        .build())
                .spiralParameters(SpiralStructureParameters.builder()
                        .numberOfArms(NUM_ARMS)
                        .armWidth(ARM_WIDTH)
                        .armRotation(ARM_ROTATION)
                        .build())
                .build();
    }

    @Test
    void shouldReturnZeroOutsideGalaxyRadius() {
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;
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

        // Core intensity is 1.0, but modulated by starNoise = (0.2 + 0.8 * JNoise[-1,
        // 1])
        // Minimum possible intensity at core should be around 0.1 or higher.
        assertThat(intensity).isGreaterThan(0.05);
    }

    @Test
    void shouldReturnPositiveIntensityOnArms() {
        // Hard to predict exact arm position without math, but should find some high
        // points
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;
        boolean foundHigh = false;

        for (int r = 20; r < GALAXY_RADIUS * 0.8; r += 10) {
            for (int angle = 0; angle < 360; angle += 15) {
                double rad = Math.toRadians(angle);
                int x = centerX + (int) (r * Math.cos(rad));
                int y = centerY + (int) (r * Math.sin(rad));
                if (generator.calculateGalaxyIntensity(x, y) > 0.3) {
                    foundHigh = true;
                    break;
                }
            }
            if (foundHigh)
                break;
        }

        assertThat(foundHigh).isTrue();
    }

    @Test
    void testGenerateBuffer() {
        float[] buffer = generator.generateBuffer();
        assertThat(buffer).hasSize(WIDTH * HEIGHT);
        // Center should be bright (modulated by noise, could be low but > 0.05)
        assertThat(buffer[(HEIGHT / 2) * WIDTH + (WIDTH / 2)]).isGreaterThan(0.05f);
    }
}
