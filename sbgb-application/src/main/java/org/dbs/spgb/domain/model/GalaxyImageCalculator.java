package org.dbs.spgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

/**
 * Calculator for generating realistic galaxy images
 * Uses GalaxyGenerator for spiral structure and color mapping for appearance
 */
@Slf4j
public class GalaxyImageCalculator {

    public static final int DEFAULT_IMAGE_WIDTH = 4000;
    public static final int DEFAULT_IMAGE_HEIGHT = 4000;
    public static final Interpolation DEFAULT_INTERPOLATION = Interpolation.COSINE;
    public static final FadeFunction DEFAULT_FADE_FUNCTION = FadeFunction.CUBIC_POLY;

    private final int width;
    private final int height;
    private final GalaxyParameters parameters;
    private final Interpolation interpolation;
    private final FadeFunction fadeFunction;
    private final GalaxyColorCalculator colorCalculator;

    private GalaxyImageCalculator(int width,
                                  int height,
                                  GalaxyParameters parameters,
                                  Interpolation interpolation,
                                  FadeFunction fadeFunction,
                                  GalaxyColorCalculator colorCalculator) {
        this.width = width;
        this.height = height;
        this.parameters = parameters;
        this.interpolation = interpolation;
        this.fadeFunction = fadeFunction;
        this.colorCalculator = colorCalculator;
    }

    public BufferedImage create(long seed) {
        log.info("Creating galaxy image {}x{} with seed {}", width, height, seed);

        // Initialize noise generator for organic texture
        PerlinGenerator noiseGenerator = new PerlinGenerator(interpolation, fadeFunction);
        noiseGenerator.createNoisePipeline(
            seed,
            width,
            height,
            parameters.getNoiseOctaves(),
            parameters.getNoisePersistence(),
            parameters.getNoiseLacunarity(),
            parameters.getNoiseScale(),
            NoiseType.FBM
        );
        noiseGenerator.performNormalization();

        // Initialize galaxy generator with geometric spiral structure
        GalaxyGenerator galaxyGenerator = GalaxyGenerator.builder()
            .width(width)
            .height(height)
            .noiseGenerator(noiseGenerator)
            .numberOfArms(parameters.getNumberOfArms())
            .armWidth(parameters.getArmWidth())
            .armRotation(parameters.getArmRotation())
            .coreSize(parameters.getCoreSize())
            .galaxyRadius(parameters.getGalaxyRadius())
            .build();

        return buildImage(galaxyGenerator);
    }

    private BufferedImage buildImage(GalaxyGenerator galaxyGenerator) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setBackground(colorCalculator.getSpaceBackgroundColor());
        g2d.clearRect(0, 0, width, height);
        g2d.dispose();

        IntStream.range(0, width).parallel().forEach(x -> {
            for (int y = 0; y < height; y++) {
                double galaxyIntensity = galaxyGenerator.calculateGalaxyIntensity(x, y);
                Color pixelColor = colorCalculator.calculateGalaxyColor(galaxyIntensity);
                img.setRGB(x, y, pixelColor.getRGB());
            }
        });

        log.info("Galaxy image generation completed");
        return img;
    }

    public static class Builder {
        private int width;
        private int height;
        private GalaxyParameters parameters;
        private Interpolation interpolation;
        private FadeFunction fadeFunction;
        private GalaxyColorCalculator colorCalculator;

        public Builder() {
            this.width = DEFAULT_IMAGE_WIDTH;
            this.height = DEFAULT_IMAGE_HEIGHT;
            this.interpolation = DEFAULT_INTERPOLATION;
            this.fadeFunction = DEFAULT_FADE_FUNCTION;
        }

        public Builder withWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder withHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder withParameters(GalaxyParameters parameters) {
            this.parameters = parameters;
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

        public Builder withColorCalculator(GalaxyColorCalculator colorCalculator) {
            this.colorCalculator = colorCalculator;
            return this;
        }

        public GalaxyImageCalculator build() {
            if (parameters == null) {
                throw new IllegalStateException("parameters must be set");
            }
            if (colorCalculator == null) {
                throw new IllegalStateException("colorCalculator must be set");
            }
            return new GalaxyImageCalculator(width, height, parameters, interpolation, fadeFunction, colorCalculator);
        }
    }
}
