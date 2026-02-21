package org.dbs.sbgb.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class SersicProfileTest {

    private static final double SERSIC_INDEX_DE_VAUCOULEURS = 4.0;
    private static final double EFFECTIVE_RADIUS = 100.0;
    private static final double TOLERANCE = 1e-6;

    @Test
    void shouldReturnOneAtEffectiveRadius() {
        SersicProfile profile = new SersicProfile(SERSIC_INDEX_DE_VAUCOULEURS, EFFECTIVE_RADIUS);

        double intensity = profile.computeIntensity(EFFECTIVE_RADIUS);

        assertThat(intensity).isCloseTo(1.0, within(TOLERANCE));
    }

    @Test
    void shouldReturnHighIntensityInsideEffectiveRadius() {
        SersicProfile profile = new SersicProfile(SERSIC_INDEX_DE_VAUCOULEURS, EFFECTIVE_RADIUS);

        double intensityInner = profile.computeIntensity(EFFECTIVE_RADIUS * 0.1);

        assertThat(intensityInner).isGreaterThan(1.0);
    }

    @Test
    void shouldDecreaseMonotonicallyWithRadius() {
        SersicProfile profile = new SersicProfile(SERSIC_INDEX_DE_VAUCOULEURS, EFFECTIVE_RADIUS);

        double i1 = profile.computeIntensity(10.0);
        double i2 = profile.computeIntensity(50.0);
        double i3 = profile.computeIntensity(100.0);
        double i4 = profile.computeIntensity(200.0);

        assertThat(i1).isGreaterThan(i2);
        assertThat(i2).isGreaterThan(i3);
        assertThat(i3).isGreaterThan(i4);
    }

    @Test
    void shouldUseCiottiApproximationForBn() {
        double n = SERSIC_INDEX_DE_VAUCOULEURS;
        double expectedBn = 2.0 * n - (1.0 / 3.0) + (4.0 / (405.0 * n)) + (46.0 / (25515.0 * n * n));
        SersicProfile profile = new SersicProfile(n, EFFECTIVE_RADIUS);

        assertThat(profile.getBn()).isCloseTo(expectedBn, within(TOLERANCE));
    }

    @Test
    void shouldReturnPositiveIntensityForAnyRadius() {
        SersicProfile profile = new SersicProfile(SERSIC_INDEX_DE_VAUCOULEURS, EFFECTIVE_RADIUS);

        assertThat(profile.computeIntensity(0.01)).isPositive();
        assertThat(profile.computeIntensity(EFFECTIVE_RADIUS)).isPositive();
        assertThat(profile.computeIntensity(EFFECTIVE_RADIUS * 10)).isPositive();
    }

    @Test
    void shouldRejectNonPositiveSersicIndex() {
        assertThatThrownBy(() -> new SersicProfile(0.0, EFFECTIVE_RADIUS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sersicIndex");
    }

    @Test
    void shouldRejectNonPositiveEffectiveRadius() {
        assertThatThrownBy(() -> new SersicProfile(SERSIC_INDEX_DE_VAUCOULEURS, 0.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("effectiveRadius");
    }
}