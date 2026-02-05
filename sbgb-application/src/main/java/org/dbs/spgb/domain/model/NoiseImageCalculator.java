package org.dbs.spgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

@Slf4j
public class NoiseImageCalculator {

    public static final int DEFAULT_IMAGE_WIDTH = 4000;
    public static final int DEFAULT_IMAGE_HEIGHT = 4000;
    public static final Interpolation DEFAULT_INTERPOLATION = Interpolation.COSINE;
    public static final FadeFunction DEFAULT_FADE_FUNCTION = FadeFunction.CUBIC_POLY;

    private final int width;
    private final int height;
    private final int octaves;
    private final double persistence;
    private final double lacunarity;
    private final double scale;
    private final NoiseType noiseType;
    private final PerlinGenerator perlinGenerator;
    private final NoiseColorCalculator noiseColorCalculator;

    private NoiseImageCalculator(int width,
                                 int height,
                                 int octaves,
                                 double persistence,
                                 double lacunarity,
                                 double scale,
                                 Interpolation interpolation,
                                 FadeFunction fadeFunction,
                                 NoiseType noiseType,
                                 NoiseColorCalculator noiseColorCalculator) {
        this.width = width;
        this.height = height;
        this.octaves = octaves;
        this.persistence = persistence;
        this.lacunarity = lacunarity;
        this.scale = scale;
        this.noiseType = noiseType;
        this.noiseColorCalculator = noiseColorCalculator;
        this.perlinGenerator = new PerlinGenerator(interpolation, fadeFunction);
    }

    public BufferedImage create(long seed) {
        perlinGenerator.createNoisePipeline(seed, this.width, this.height, this.octaves, this.persistence, this.lacunarity, this.scale, this.noiseType);
        perlinGenerator.performNormalization();
        return buildImage();
    }

    private BufferedImage buildImage() {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setBackground(noiseColorCalculator.getBackGroundColor());
        g2d.clearRect(0, 0, width, height);
        g2d.dispose();

        IntStream.range(0, width).parallel().forEach(x -> {
            for (int y = 0; y < height; y++) {
                img.setRGB(x, y, noiseColorCalculator.calculateNoiseColor(perlinGenerator.scaleNoiseNormalizedValue(x, y)).getRGB());
            }
        });

        return img;
    }


    public static class Builder {
        private int width;
        private int height;
        private int octaves;
        private double persistence;
        private double lacunarity;
        private double scale;
        private NoiseType noiseType;
        private Interpolation interpolation;
        private FadeFunction fadeFunction;
        private NoiseColorCalculator noiseColorCalculator;
        public Builder() {
            this.width = DEFAULT_IMAGE_WIDTH;
            this.height = DEFAULT_IMAGE_HEIGHT;
            this.octaves = 1;
            this.persistence = 0.5;
            this.lacunarity = 2.0;
            this.scale = 100.0;
            this.noiseType = NoiseType.FBM;
            this.interpolation = DEFAULT_INTERPOLATION;
            this.fadeFunction = DEFAULT_FADE_FUNCTION;
        }

        public Builder withWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder withOctaves(int octaves) {
            this.octaves = octaves;
            return this;
        }

        public Builder withPersistence(double persistence) {
            this.persistence = persistence;
            return this;
        }

        public Builder withLacunarity(double lacunarity) {
            this.lacunarity = lacunarity;
            return this;
        }

        public Builder withScale(double scale) {
            this.scale = scale;
            return this;
        }

        public Builder withNoiseType(NoiseType noiseType) {
            this.noiseType = noiseType;
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

        public NoiseImageCalculator build() {
            return new NoiseImageCalculator(width, height, octaves, persistence, lacunarity, scale, interpolation, fadeFunction, noiseType, noiseColorCalculator);
        }
    }

}