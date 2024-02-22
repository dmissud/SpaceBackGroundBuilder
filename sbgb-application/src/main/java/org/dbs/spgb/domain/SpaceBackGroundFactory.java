package org.dbs.spgb.domain;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import de.articdive.jnoise.pipeline.JNoise;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;

@Slf4j
public class SpaceBackGroundFactory {

    private final int width;
    private final int height;
    private final Interpolation interpolation;
    private final FadeFunction fadeFunction;
    private JNoise noisePipeline;

    private SpaceBackGroundFactory(int width, int height, Interpolation interpolation, FadeFunction fadeFunction) {
        this.width = width;
        this.height = height;
        this.interpolation = interpolation;
        this.fadeFunction = fadeFunction;
    }

    public  BufferedImage create(long seed) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        setGraphicsBgColor(g2d);

        createNoisePipeline(seed);

        double[] noiseValues = getMinMaxNoiseValues();

        double minVal = noiseValues[0];
        double maxVal = noiseValues[1];

        updateGraphicsColors(g2d, minVal, maxVal);
        return img;
    }

    private void setGraphicsBgColor(Graphics2D g2d) {
        // Setting the background as transparent
        g2d.setBackground(new Color(0, 0, 0, 0)); // The last parameter sets alpha (transparency)
        g2d.clearRect(0, 0, width, height); // Clear the entire area to the background color (transparent)
    }

    private void createNoisePipeline(long seed) { // Interpolation.COSINE, FadeFunction.CUBIC_POLY
        this.noisePipeline = JNoise.newBuilder().perlin(seed, this.interpolation, this.fadeFunction)
                .scale(100)
                .clamp(0.0, 3.0)
                .build();
    }

    private double[] getMinMaxNoiseValues() {
        double maxVal = 0;
        double minVal = 1.0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double noiseVal = scaleNoiseValue(x, y);
                if (noiseVal > maxVal) maxVal = noiseVal;
                if (noiseVal < minVal) minVal = noiseVal;
            }
        }
        return new double[]{minVal, maxVal};
    }

    private double scaleNoiseValue(int x, int y) {
        return noisePipeline.evaluateNoise(x / (width * 1.0), y / (height * 1.0));
    }

    private void updateGraphicsColors(Graphics2D g2d, double minVal, double maxVal) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double noiseVal = scaleNoiseValue(x, y);
                noiseVal = normalizeNoiseValue(noiseVal, minVal, maxVal);
                Color newColor = calculateColor(noiseVal);
                g2d.setColor(newColor);
                g2d.drawLine(x, y, x, y);
            }
        }
    }

    private double normalizeNoiseValue(double noiseVal, double minVal, double maxVal) {
        return (noiseVal - minVal) / (maxVal - minVal);
    }

    private Color calculateColor(double noiseVal) {
        double blackR = 0;
        double blackG = 0;
        double blackB = 0;
        double orangeR = 255;
        double orangeG = 165;
        double orangeB = 0;
        double whiteR = 255;
        double whiteG = 255;
        double whiteB = 255;

        if (noiseVal < 0.7) {
            return Color.BLACK;
        } else if (noiseVal < 0.75) {
            return calculateIntermediateColor(noiseVal, blackR, orangeR, blackG, orangeG, blackB, orangeB);
        } else {
            return calculateIntermediateColor(noiseVal, blackR, whiteR, blackG, whiteG, blackB, whiteB);
        }
    }

    private Color calculateIntermediateColor(double noiseVal, double r1, double r2, double g1, double g2, double b1, double b2) {
        double newR = ((1 - noiseVal) * r1 + noiseVal * r2);
        double newG = ((1 - noiseVal) * g1 + noiseVal * g2);
        double newB = ((1 - noiseVal) * b1 + noiseVal * b2);
        newR = Math.max(0, Math.min(255, newR));
        newG = Math.max(0, Math.min(255, newG));
        newB = Math.max(0, Math.min(255, newB));
        return new Color((int) newR, (int) newG, (int) newB);
    }

    public static class Builder {
        private int width;
        private int height;
        private Interpolation interpolation;
        private FadeFunction fadeFunction;

        public Builder() {
            this.width = 4000;
            this.height = 4000;
            this.interpolation = Interpolation.COSINE;
            this.fadeFunction = FadeFunction.CUBIC_POLY;
        }

        public Builder withWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder withHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder withInterpolation(Interpolation interpolation) {
            this.interpolation = interpolation;
            return this;
        }

        public Builder withFadeFunction(FadeFunction fadeFunction) {
            this.fadeFunction = fadeFunction;
            return this;
        }

        public SpaceBackGroundFactory build() {
            return new SpaceBackGroundFactory(width, height, interpolation, fadeFunction);
        }
    }

}