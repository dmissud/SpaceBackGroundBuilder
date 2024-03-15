package org.dbs.spgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;

@Slf4j
public class SpaceBackGround {

    private final int width;
    private final int height;
    private final PerlinGenerator perlinGenerator;
    private final NoiseColorCalculator noiseColorCalculator;

    private SpaceBackGround(int width,
                            int height,
                            Interpolation interpolation,
                            FadeFunction fadeFunction,
                            NoiseColorCalculator noiseColorCalculator) {
        this.width = width;
        this.height = height;
        this.noiseColorCalculator = noiseColorCalculator;
        this.perlinGenerator = new PerlinGenerator(interpolation, fadeFunction);
    }

    public BufferedImage create(long seed) {
        perlinGenerator.createNoisePipeline(seed, this.width, this.height);
        return buildImage();
    }

    private BufferedImage buildImage() {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setBackground(noiseColorCalculator.getBackGroundColor());
        g2d.clearRect(0, 0, width, height);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                g2d.setColor(noiseColorCalculator.calculateNoiseColor(perlinGenerator.scaleNoiseNormalizedValue(x, y)));
                g2d.drawLine(x, y, x, y);
            }
        }

        return img;
    }


    public static class Builder {
        private int width;
        private int height;
        private Interpolation interpolation;
        private FadeFunction fadeFunction;
        private NoiseColorCalculator noiseColorCalculator;
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

        public Builder withNoiseColorCalculator(NoiseColorCalculator noiseColorCalculator) {
            this.noiseColorCalculator = noiseColorCalculator;
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

        public SpaceBackGround build() {
            return new SpaceBackGround(width, height, interpolation, fadeFunction, noiseColorCalculator);
        }
    }

}