package org.dbs.spgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * Multi-layer noise generator combining three octaves at different scales:
 * - Macro: large-scale structures (lower frequency)
 * - Meso: medium-scale details (medium frequency)
 * - Micro: fine-scale grain (higher frequency)
 *
 * Each layer is weighted and combined to produce rich, multi-scale noise patterns.
 */
@Slf4j
@Builder
public class MultiLayerNoiseGenerator {
    private final long seed;
    private final int width;
    private final int height;
    private final Interpolation interpolation;
    private final FadeFunction fadeFunction;
    private final NoiseType noiseType;

    private final double macroScale;
    private final double macroWeight;
    private final double mesoScale;
    private final double mesoWeight;
    private final double microScale;
    private final double microWeight;

    private PerlinGenerator macroLayer;
    private PerlinGenerator mesoLayer;
    private PerlinGenerator microLayer;

    private double minVal;
    private double maxVal;

    /**
     * Initialize the three noise layers with independent seeds.
     */
    public void initialize() {
        // Create three PerlinGenerators with offset seeds to ensure independence
        macroLayer = new PerlinGenerator(interpolation, fadeFunction);
        mesoLayer = new PerlinGenerator(interpolation, fadeFunction);
        microLayer = new PerlinGenerator(interpolation, fadeFunction);

        // Use seed offsets to ensure each layer is independent
        macroLayer.createNoisePipeline(seed, width, height, 3, 0.5, 2.0, macroScale, noiseType);
        mesoLayer.createNoisePipeline(seed + 1000, width, height, 3, 0.5, 2.0, mesoScale, noiseType);
        microLayer.createNoisePipeline(seed + 2000, width, height, 3, 0.5, 2.0, microScale, noiseType);

        macroLayer.performNormalization();
        mesoLayer.performNormalization();
        microLayer.performNormalization();

        normalizeMinMaxValues();

        log.info("MultiLayerNoiseGenerator initialized: seed={}, macro={}/{}, meso={}/{}, micro={}/{}",
                seed, macroScale, macroWeight, mesoScale, mesoWeight, microScale, microWeight);
    }

    /**
     * Evaluate the multi-layer noise at the given pixel coordinates.
     *
     * @param x pixel x-coordinate
     * @param y pixel y-coordinate
     * @return combined noise value normalized to [0, 1]
     */
    public double evaluate(int x, int y) {
        double macroValue = macroLayer.scaleNoiseNormalizedValue(x, y);
        double mesoValue = mesoLayer.scaleNoiseNormalizedValue(x, y);
        double microValue = microLayer.scaleNoiseNormalizedValue(x, y);

        // Weighted combination
        double combined = macroValue * macroWeight +
                mesoValue * mesoWeight +
                microValue * microWeight;

        // Normalize to [0, 1]
        return normalize(combined);
    }

    private void normalizeMinMaxValues() {
        double currentMax = Double.NEGATIVE_INFINITY;
        double currentMin = Double.POSITIVE_INFINITY;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double macroValue = macroLayer.scaleNoiseNormalizedValue(x, y);
                double mesoValue = mesoLayer.scaleNoiseNormalizedValue(x, y);
                double microValue = microLayer.scaleNoiseNormalizedValue(x, y);

                double combined = macroValue * macroWeight +
                        mesoValue * mesoWeight +
                        microValue * microWeight;

                if (combined > currentMax) currentMax = combined;
                if (combined < currentMin) currentMin = combined;
            }
        }

        this.maxVal = currentMax;
        this.minVal = currentMin;

        log.info("MultiLayerNoiseGenerator normalized: min={}, max={}", minVal, maxVal);
    }

    private double normalize(double value) {
        if (maxVal == minVal) {
            return 0.5; // Avoid division by zero
        }
        return (value - minVal) / (maxVal - minVal);
    }
}
