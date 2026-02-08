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
        log.info("Creating galaxy image {}x{} with seed {} type {}", width, height, seed, parameters.getGalaxyType());

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

        GalaxyIntensityCalculator intensityCalculator = createIntensityCalculator(noiseGenerator, seed);

        return buildImage(intensityCalculator);
    }

    private GalaxyIntensityCalculator createIntensityCalculator(PerlinGenerator noiseGenerator, long seed) {
        return switch (parameters.getGalaxyType()) {
            case VORONOI_CLUSTER -> VoronoiClusterGalaxyGenerator.builder()
                    .width(width)
                    .height(height)
                    .noiseGenerator(noiseGenerator)
                    .seed(seed)
                    .coreSize(parameters.getCoreSize())
                    .galaxyRadius(parameters.getGalaxyRadius())
                    .clusterCount(parameters.getClusterCount() != null ? parameters.getClusterCount() : 80)
                    .clusterSize(parameters.getClusterSize() != null ? parameters.getClusterSize() : 60.0)
                    .clusterConcentration(parameters.getClusterConcentration() != null ? parameters.getClusterConcentration() : 0.7)
                    .build();
            case SPIRAL -> GalaxyGenerator.builder()
                    .width(width)
                    .height(height)
                    .noiseGenerator(noiseGenerator)
                    .numberOfArms(parameters.getNumberOfArms())
                    .armWidth(parameters.getArmWidth())
                    .armRotation(parameters.getArmRotation())
                    .coreSize(parameters.getCoreSize())
                    .galaxyRadius(parameters.getGalaxyRadius())
                    .build();
            case ELLIPTICAL -> EllipticalGalaxyGenerator.builder()
                    .width(width)
                    .height(height)
                    .noiseGenerator(noiseGenerator)
                    .coreSize(parameters.getCoreSize())
                    .galaxyRadius(parameters.getGalaxyRadius())
                    .sersicIndex(parameters.getSersicIndex() != null ? parameters.getSersicIndex() : 4.0)
                    .axisRatio(parameters.getAxisRatio() != null ? parameters.getAxisRatio() : 0.7)
                    .orientationAngle(parameters.getOrientationAngle() != null ? parameters.getOrientationAngle() : 0.0)
                    .build();
        };
    }

    private BufferedImage buildImage(GalaxyIntensityCalculator intensityCalculator) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setBackground(colorCalculator.getSpaceBackgroundColor());
        g2d.clearRect(0, 0, width, height);
        g2d.dispose();

        IntStream.range(0, width).parallel().forEach(x -> {
            for (int y = 0; y < height; y++) {
                double galaxyIntensity = intensityCalculator.calculateGalaxyIntensity(x, y);
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
