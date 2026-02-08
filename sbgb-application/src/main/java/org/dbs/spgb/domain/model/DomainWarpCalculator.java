package org.dbs.spgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import lombok.extern.slf4j.Slf4j;

/**
 * Calculator for domain warping effect
 * Deforms space using noise before sampling galaxy intensity, creating organic filamentary structures
 */
@Slf4j
public class DomainWarpCalculator {

    private final PerlinGenerator warpXGenerator;
    private final PerlinGenerator warpYGenerator;
    private final double warpStrength;
    private final int width;
    private final int height;

    public DomainWarpCalculator(int width, int height, double warpStrength, long seed,
                                Interpolation interpolation, FadeFunction fadeFunction) {
        this.width = width;
        this.height = height;
        this.warpStrength = warpStrength;

        // Create two independent noise generators for X and Y warping with different seeds
        this.warpXGenerator = new PerlinGenerator(interpolation, fadeFunction);
        this.warpXGenerator.createNoisePipeline(
            seed + 12345, // Offset seed for X
            width,
            height,
            4,         // octaves
            0.5,       // persistence
            2.0,       // lacunarity
            100.0,     // scale (large features)
            NoiseType.FBM
        );
        this.warpXGenerator.performNormalization();

        this.warpYGenerator = new PerlinGenerator(interpolation, fadeFunction);
        this.warpYGenerator.createNoisePipeline(
            seed + 67890, // Different seed for Y
            width,
            height,
            4,
            0.5,
            2.0,
            100.0,
            NoiseType.FBM
        );
        this.warpYGenerator.performNormalization();

        log.info("Domain warp calculator initialized with strength {}", warpStrength);
    }

    /**
     * Apply domain warping to coordinates
     * Returns warped coordinates as [warpedX, warpedY]
     */
    public double[] warpCoordinates(int x, int y) {
        if (warpStrength == 0.0) {
            return new double[]{x, y};
        }

        // Get noise values in range [0, 1] and center them to [-0.5, 0.5]
        double noiseX = warpXGenerator.scaleNoiseNormalizedValue(x, y) - 0.5;
        double noiseY = warpYGenerator.scaleNoiseNormalizedValue(x, y) - 0.5;

        // Apply warp displacement
        double warpedX = x + noiseX * warpStrength;
        double warpedY = y + noiseY * warpStrength;

        return new double[]{warpedX, warpedY};
    }

    /**
     * Check if warping is enabled
     */
    public boolean isEnabled() {
        return warpStrength > 0.0;
    }
}
