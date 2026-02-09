package org.dbs.sbgb.domain.constant;

/**
 * Constants for noise modulation in galaxy intensity calculations.
 * Noise adds organic variation to prevent artificial-looking patterns.
 */
public final class NoiseModulationConstants {

    private NoiseModulationConstants() {
        // Utility class
    }

    /**
     * Base contribution of noise to intensity (minimum noise effect).
     * Used in: spiral, voronoi, elliptical galaxies
     */
    public static final double NOISE_BASE_CONTRIBUTION = 0.3;

    /**
     * Variable contribution of noise to intensity (noise modulation range).
     * Used in: spiral, voronoi galaxies
     * Formula: noiseFactor = BASE + (noiseValue * MODULATION)
     */
    public static final double NOISE_MODULATION_RANGE = 0.7;

    /**
     * Base contribution for elliptical galaxies (subtle noise).
     * Elliptical galaxies have smoother profiles with less noise.
     */
    public static final double ELLIPTICAL_NOISE_BASE = 0.7;

    /**
     * Modulation range for elliptical galaxies (minimal variation).
     */
    public static final double ELLIPTICAL_NOISE_RANGE = 0.3;

    /**
     * Base contribution for ring galaxies (strong noise suppression).
     * Ring galaxies have very defined structures.
     */
    public static final double RING_NOISE_BASE = 0.8;

    /**
     * Modulation range for ring galaxies (minimal variation).
     */
    public static final double RING_NOISE_RANGE = 0.2;
}
