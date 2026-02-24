package org.dbs.sbgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import de.articdive.jnoise.modules.octavation.fractal_functions.FractalFunction;
import de.articdive.jnoise.pipeline.JNoise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PerlinGenerator {
    final Interpolation interpolation;
    final FadeFunction fadeFunction;
    private JNoise noisePipeline;
    private int width;
    private int height;
    private double maxVal;
    private double minVal;

    public PerlinGenerator(Interpolation interpolation, FadeFunction fadeFunction) {
        this.interpolation = interpolation;
        this.fadeFunction = fadeFunction;
    }

    public void createNoisePipeline(long seed, int width, int height, int octaves, double persistence, double lacunarity, double scale, NoiseType noiseType) {
        FractalFunction fractalFunction = noiseType == NoiseType.RIDGED
                ? FractalFunction.RIDGED_MULTI
                : FractalFunction.FBM;

        noisePipeline = JNoise.newBuilder()
                .perlin(seed, this.interpolation, this.fadeFunction)
                .scale(scale)
                .octavate(octaves, persistence, lacunarity, fractalFunction, true)
                .clamp(0.0, 3.0)
                .build();
        this.width = width;
        this.height = height;
    }


    public double scaleNoiseNormalizedValue(int x, int y) {
        return normalizeNoiseValue(scaleNoiseValue(x, y));
    }

    public void performNormalization() {
        normalizeMinimumAndMaximumValues();
        log.info("Normalized({}, {})", this.minVal, this.maxVal);
    }

    private double scaleNoiseValue(int x, int y) {
        return this.noisePipeline.evaluateNoise(x * 1.0 / this.width, y * 1.0 / this.height);
    }

    private double normalizeNoiseValue(double noiseVal) {
        return (noiseVal - this.minVal) / (this.maxVal - this.minVal);
    }


    private void normalizeMinimumAndMaximumValues() {
        double currentMax = Double.NEGATIVE_INFINITY;
        double currentMin = Double.POSITIVE_INFINITY;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double noiseVal = scaleNoiseValue(x, y);
                if (noiseVal > currentMax) currentMax = noiseVal;
                if (noiseVal < currentMin) currentMin = noiseVal;
            }
        }
        this.maxVal = currentMax;
        this.minVal = currentMin;
    }
}