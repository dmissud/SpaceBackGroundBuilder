package org.dbs.sbgb.domain.constant;

/**
 * Constants for radial falloff calculations in galaxy intensity.
 * Radial falloff creates the natural dimming from center to edge.
 */
public final class RadialFalloffConstants {

    private RadialFalloffConstants() {
        // Utility class
    }

    /**
     * Standard falloff exponent for most galaxy types.
     * Creates a quadratic falloff: intensity ∝ (1 - r)²
     * Used in: spiral, voronoi, ring galaxies
     */
    public static final double STANDARD_FALLOFF_EXPONENT = 2.0;

    /**
     * Gaussian denominator for intensity calculations.
     * Used in Gaussian profiles: exp(-(d²) / (2 * σ²))
     */
    public static final double GAUSSIAN_DENOMINATOR = 2.0;
}
