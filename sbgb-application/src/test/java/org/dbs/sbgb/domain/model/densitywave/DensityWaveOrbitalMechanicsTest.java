package org.dbs.sbgb.domain.model.densitywave;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class DensityWaveOrbitalMechanicsTest {

    private static final float GALAXY_RADIUS = 15000.0f;
    private static final float CORE_RADIUS = 2000.0f;
    private static final float EX1 = 0.9f;
    private static final float EX2 = 0.72f;
    private static final float ANGLE_OFFSET = 0.00019f;

    private DensityWaveOrbitalMechanics mechanics;

    @BeforeEach
    void setUp() {
        mechanics = new DensityWaveOrbitalMechanics(GALAXY_RADIUS, CORE_RADIUS, EX1, EX2, ANGLE_OFFSET);
    }

    @Test
    void eccentricity_in_core_is_close_to_one_at_center() {
        assertThat(mechanics.eccentricity(0)).isCloseTo(1.0f, within(0.01f));
    }

    @Test
    void eccentricity_at_core_boundary_equals_ex1() {
        assertThat(mechanics.eccentricity(CORE_RADIUS)).isCloseTo(EX1, within(0.01f));
    }

    @Test
    void eccentricity_at_galaxy_boundary_equals_ex2() {
        assertThat(mechanics.eccentricity(GALAXY_RADIUS)).isCloseTo(EX2, within(0.01f));
    }

    @Test
    void eccentricity_beyond_far_field_is_one() {
        assertThat(mechanics.eccentricity(GALAXY_RADIUS * 3)).isCloseTo(1.0f, within(0.01f));
    }

    @Test
    void angular_offset_is_proportional_to_radius() {
        float r1 = 5000f;
        float r2 = 10000f;
        assertThat(mechanics.angularOffset(r2)).isCloseTo(mechanics.angularOffset(r1) * 2, within(0.01f));
    }

    @Test
    void orbital_velocity_is_positive_for_nonzero_radius() {
        assertThat(mechanics.orbitalVelocity(5000f)).isGreaterThan(0.0);
    }

    @Test
    void orbital_velocity_is_zero_for_zero_radius() {
        assertThat(mechanics.orbitalVelocity(0f)).isEqualTo(0.0);
    }

    @Test
    void orbital_velocity_decreases_with_distance_newtonian() {
        double v1 = mechanics.orbitalVelocity(2000f);
        double v2 = mechanics.orbitalVelocity(10000f);
        assertThat(v1).isGreaterThan(v2);
    }
}
