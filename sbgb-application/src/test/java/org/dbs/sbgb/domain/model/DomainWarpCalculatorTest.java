package org.dbs.sbgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainWarpCalculatorTest {

    @Test
    void shouldReturnOriginalCoordinatesWhenWarpStrengthIsZero() {
        // Given
        DomainWarpCalculator calculator = new DomainWarpCalculator(
            1000, 1000, 0.0, 12345L,
            Interpolation.COSINE, FadeFunction.CUBIC_POLY
        );

        // When
        double[] warped = calculator.warpCoordinates(500, 500);

        // Then
        assertThat(warped[0]).isEqualTo(500.0);
        assertThat(warped[1]).isEqualTo(500.0);
        assertThat(calculator.isEnabled()).isFalse();
    }

    @Test
    void shouldWarpCoordinatesWhenWarpStrengthIsPositive() {
        // Given
        DomainWarpCalculator calculator = new DomainWarpCalculator(
            1000, 1000, 50.0, 12345L,
            Interpolation.COSINE, FadeFunction.CUBIC_POLY
        );

        // When
        double[] warped = calculator.warpCoordinates(500, 500);

        // Then
        assertThat(warped[0]).isNotEqualTo(500.0);
        assertThat(warped[1]).isNotEqualTo(500.0);
        assertThat(calculator.isEnabled()).isTrue();
    }

    @Test
    void shouldProduceConsistentWarpingForSameSeed() {
        // Given
        DomainWarpCalculator calculator1 = new DomainWarpCalculator(
            1000, 1000, 100.0, 42L,
            Interpolation.COSINE, FadeFunction.CUBIC_POLY
        );
        DomainWarpCalculator calculator2 = new DomainWarpCalculator(
            1000, 1000, 100.0, 42L,
            Interpolation.COSINE, FadeFunction.CUBIC_POLY
        );

        // When
        double[] warped1 = calculator1.warpCoordinates(300, 400);
        double[] warped2 = calculator2.warpCoordinates(300, 400);

        // Then
        assertThat(warped1[0]).isEqualTo(warped2[0]);
        assertThat(warped1[1]).isEqualTo(warped2[1]);
    }

    @Test
    void shouldProduceReproducibleWarpingWithSameSeedAndParameters() {
        // Given
        DomainWarpCalculator calculator = new DomainWarpCalculator(
            1000, 1000, 100.0, 42L,
            Interpolation.COSINE, FadeFunction.CUBIC_POLY
        );

        // When - warp the same point multiple times
        double[] warped1 = calculator.warpCoordinates(500, 500);
        double[] warped2 = calculator.warpCoordinates(500, 500);
        double[] warped3 = calculator.warpCoordinates(500, 500);

        // Then - all calls should produce identical results (reproducibility)
        assertThat(warped1[0]).isEqualTo(warped2[0]).isEqualTo(warped3[0]);
        assertThat(warped1[1]).isEqualTo(warped2[1]).isEqualTo(warped3[1]);
    }

    @Test
    void shouldScaleDisplacementWithWarpStrength() {
        // Given
        DomainWarpCalculator weakWarp = new DomainWarpCalculator(
            1000, 1000, 10.0, 12345L,
            Interpolation.COSINE, FadeFunction.CUBIC_POLY
        );
        DomainWarpCalculator strongWarp = new DomainWarpCalculator(
            1000, 1000, 200.0, 12345L,
            Interpolation.COSINE, FadeFunction.CUBIC_POLY
        );

        // When
        double[] warpedWeak = weakWarp.warpCoordinates(500, 500);
        double[] warpedStrong = strongWarp.warpCoordinates(500, 500);

        // Then
        double displacementWeak = Math.sqrt(
            Math.pow(warpedWeak[0] - 500, 2) + Math.pow(warpedWeak[1] - 500, 2)
        );
        double displacementStrong = Math.sqrt(
            Math.pow(warpedStrong[0] - 500, 2) + Math.pow(warpedStrong[1] - 500, 2)
        );

        assertThat(displacementStrong).isGreaterThan(displacementWeak);
    }
}
