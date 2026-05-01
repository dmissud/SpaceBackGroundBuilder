package org.dbs.sbgb.domain.model.densitywave;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DensityWaveIntensityCalculatorTest {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;
    private static final long SEED = 42L;
    private static final float GALAXY_RADIUS = 15000.0f;

    private DensityWaveIntensityCalculator calculator;

    @BeforeEach
    void setUp() {
        DensityWaveGalaxyParams params = DensityWaveGalaxyParams.defaultParams(GALAXY_RADIUS, 2000);
        calculator = new DensityWaveIntensityCalculator(WIDTH, HEIGHT, SEED, params);
    }

    @Test
    void intensity_at_center_is_high() {
        double centerIntensity = calculator.calculateGalaxyIntensity(WIDTH / 2, HEIGHT / 2);
        assertThat(centerIntensity).isGreaterThan(0.05);
    }

    @Test
    void intensity_at_corner_is_lower_than_center() {
        double centerIntensity = calculator.calculateGalaxyIntensity(WIDTH / 2, HEIGHT / 2);
        double cornerIntensity = calculator.calculateGalaxyIntensity(0, 0);
        assertThat(cornerIntensity).isLessThan(centerIntensity);
    }

    @Test
    void intensity_is_in_range_zero_to_one() {
        for (int x = 0; x < WIDTH; x += 40) {
            for (int y = 0; y < HEIGHT; y += 40) {
                double intensity = calculator.calculateGalaxyIntensity(x, y);
                assertThat(intensity).isBetween(0.0, 1.0);
            }
        }
    }

    @Test
    void intensity_at_center_is_nonzero() {
        double totalIntensity = 0;
        for (int x = WIDTH / 4; x < 3 * WIDTH / 4; x += 10) {
            for (int y = HEIGHT / 4; y < 3 * HEIGHT / 4; y += 10) {
                totalIntensity += calculator.calculateGalaxyIntensity(x, y);
            }
        }
        assertThat(totalIntensity).isGreaterThan(0.0);
    }
}
