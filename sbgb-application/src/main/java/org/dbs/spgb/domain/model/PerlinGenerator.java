package org.dbs.spgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
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

    public void createNoisePipeline(long seed, int width, int height) { // Interpolation.COSINE, FadeFunction.CUBIC_POLY
        noisePipeline = JNoise.newBuilder().perlin(seed, this.interpolation, this.fadeFunction)
                .scale(100)
                .clamp(0.0, 3.0)
                .build();
        this.width = width;
        this.height = height;
        normalize();
    }

    public double scaleNoiseNormalizedValue(int x, int y) {
        return normalizeNoiseValue(scaleNoiseValue(x, y));
    }
    private double scaleNoiseValue(int x, int y) {
        return this.noisePipeline.evaluateNoise(x * 1.0 / this.width, y * 1.0 / this.height);
    }

    private double normalizeNoiseValue(double noiseVal) {
        return (noiseVal - this.minVal) / (this.maxVal - this.minVal);
    }


    private void normalize() {
        this.maxVal = 0;
        this.minVal = 1.0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double noiseVal = scaleNoiseValue(x, y);
                if (noiseVal > maxVal) maxVal = noiseVal;
                if (noiseVal < minVal) minVal = noiseVal;
            }
        }
        log.info("Normalized({}, {})", this.minVal, this.maxVal);
    }


}