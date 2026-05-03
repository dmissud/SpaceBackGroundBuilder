package org.dbs.sbgb.domain.model.particle;

import org.dbs.sbgb.domain.model.parameters.EllipticalShapeParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EllipticalParticleIntensityCalculatorTest {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;

    private EllipticalParticleIntensityCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new EllipticalParticleIntensityCalculator(WIDTH, HEIGHT,
                15000f, 2000, EllipticalShapeParameters.builder().build(), 42L);
    }

    @Test
    void intensity_at_center_is_high() {
        assertThat(calculator.calculateGalaxyIntensity(WIDTH / 2, HEIGHT / 2)).isGreaterThan(0.1);
    }

    @Test
    void intensity_at_corner_is_lower_than_center() {
        double center = calculator.calculateGalaxyIntensity(WIDTH / 2, HEIGHT / 2);
        double corner = calculator.calculateGalaxyIntensity(0, 0);
        assertThat(corner).isLessThan(center);
    }

    @Test
    void intensity_in_range_zero_to_one() {
        for (int x = 0; x < WIDTH; x += 40) {
            for (int y = 0; y < HEIGHT; y += 40) {
                assertThat(calculator.calculateGalaxyIntensity(x, y)).isBetween(0.0, 1.0);
            }
        }
    }

    @Test
    void intensity_out_of_bounds_returns_zero() {
        assertThat(calculator.calculateGalaxyIntensity(-1, 0)).isZero();
        assertThat(calculator.calculateGalaxyIntensity(0, HEIGHT)).isZero();
    }
}
