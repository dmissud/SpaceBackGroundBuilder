package org.dbs.sbgb.domain.constant;

/**
 * Constants for core intensity calculations in galaxy centers.
 * Core represents the bright central bulge of the galaxy.
 */
public final class CoreIntensityConstants {

    private CoreIntensityConstants() {
        // Utility class
    }

    /**
     * Core exponential falloff rate.
     * Higher values = steeper falloff from center.
     * Used in: spiral, voronoi galaxies
     * Formula: exp(-distance * FALLOFF_RATE)
     */
    public static final double CORE_EXPONENTIAL_FALLOFF = 3.0;

    /**
     * Core brightness multiplier for spiral/voronoi galaxies.
     * Amplifies the core intensity for a brighter center.
     */
    public static final double CORE_BRIGHTNESS_MULTIPLIER = 2.0;

    /**
     * Weak core intensity multiplier for irregular galaxies.
     * Irregular galaxies have less prominent cores.
     */
    public static final double IRREGULAR_CORE_MULTIPLIER = 0.3;

    /**
     * Clump intensity contribution weight for irregular galaxies.
     * Controls how much clumps contribute to total intensity.
     */
    public static final double IRREGULAR_CLUMP_WEIGHT = 0.7;
}
