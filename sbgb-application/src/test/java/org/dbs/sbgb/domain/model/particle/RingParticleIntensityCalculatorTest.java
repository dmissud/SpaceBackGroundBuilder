package org.dbs.sbgb.domain.model.particle;

import org.dbs.sbgb.domain.model.parameters.CoreParameters;
import org.dbs.sbgb.domain.model.parameters.RingStructureParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RingParticleIntensityCalculatorTest {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;

    private RingParticleIntensityCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new RingParticleIntensityCalculator(WIDTH, HEIGHT,
                CoreParameters.builder().galaxyRadius(1500).coreSize(0.05).build(),
                RingStructureParameters.builder().ringRadius(450).ringWidth(50).build(),
                42L);
    }

    @Test
    void intensity_on_ring_is_higher_than_at_center() {
        int cx = WIDTH / 2;
        int cy = HEIGHT / 2;
        double ringScale = (double) Math.min(WIDTH, HEIGHT) / (2.0 * 1500 * 0.4);
        int ringPx = (int) (450 * ringScale);

        double centerIntensity = calculator.calculateGalaxyIntensity(cx, cy);
        double ringIntensity = calculator.calculateGalaxyIntensity(cx + ringPx, cy);

        assertThat(ringIntensity).isGreaterThan(centerIntensity);
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
        assertThat(calculator.calculateGalaxyIntensity(WIDTH, 0)).isZero();
    }
}
