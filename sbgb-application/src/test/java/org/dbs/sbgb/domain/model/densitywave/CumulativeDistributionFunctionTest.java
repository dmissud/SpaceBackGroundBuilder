package org.dbs.sbgb.domain.model.densitywave;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class CumulativeDistributionFunctionTest {

    private static final double GALAXY_RADIUS = 15000.0;
    private static final double CORE_RADIUS = 2000.0;
    private static final int STEPS = 1000;

    private CumulativeDistributionFunction cdf;

    @BeforeEach
    void setUp() {
        cdf = new CumulativeDistributionFunction();
        cdf.setupRealistic(1.0, 0.02, GALAXY_RADIUS / 3.0, CORE_RADIUS, 0.0, GALAXY_RADIUS * 2, STEPS);
    }

    @Test
    void valFromProb_zero_returns_minimum() {
        assertThat(cdf.valFromProb(0.0)).isCloseTo(0.0, within(1.0));
    }

    @Test
    void valFromProb_one_returns_maximum() {
        assertThat(cdf.valFromProb(1.0)).isCloseTo(GALAXY_RADIUS * 2, within(100.0));
    }

    @Test
    void valFromProb_is_monotonically_increasing() {
        double previous = cdf.valFromProb(0.0);
        for (int i = 1; i <= 10; i++) {
            double current = cdf.valFromProb(i / 10.0);
            assertThat(current).isGreaterThanOrEqualTo(previous);
            previous = current;
        }
    }

    @Test
    void valFromProb_median_is_within_galaxy_radius() {
        double median = cdf.valFromProb(0.5);
        assertThat(median).isBetween(0.0, GALAXY_RADIUS * 2);
    }

    @Test
    void probFromVal_and_valFromProb_are_inverse() {
        double originalRadius = 5000.0;
        double prob = cdf.probFromVal(originalRadius);
        double recovered = cdf.valFromProb(prob);
        assertThat(recovered).isCloseTo(originalRadius, within(50.0));
    }

    @Test
    void distribution_concentrates_stars_near_center() {
        double halfMassRadius = cdf.valFromProb(0.5);
        assertThat(halfMassRadius).isLessThan(GALAXY_RADIUS);
    }

    @Test
    void bulge_region_has_higher_density_than_disc() {
        double probAtBulge = cdf.probFromVal(CORE_RADIUS);
        double probAtDisc = cdf.probFromVal(GALAXY_RADIUS) - cdf.probFromVal(GALAXY_RADIUS * 0.5);
        assertThat(probAtBulge).isGreaterThan(0.0);
    }
}
