package org.dbs.sbgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.constant.GalaxyDefaults;
import org.dbs.sbgb.domain.factory.NoiseGeneratorFactory;
import org.dbs.sbgb.domain.service.StarFieldApplicator;
import org.dbs.sbgb.domain.strategy.GalaxyGenerationContext;
import org.dbs.sbgb.domain.strategy.GalaxyGeneratorFactory;

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
    private final GalaxyGeneratorFactory generatorFactory;
    private final NoiseGeneratorFactory noiseGeneratorFactory;
    private final StarFieldApplicator starFieldApplicator;

    private GalaxyImageCalculator(int width,
            int height,
            GalaxyParameters parameters,
            Interpolation interpolation,
            FadeFunction fadeFunction,
            GalaxyColorCalculator colorCalculator,
            GalaxyGeneratorFactory generatorFactory,
            NoiseGeneratorFactory noiseGeneratorFactory,
            StarFieldApplicator starFieldApplicator) {
        this.width = width;
        this.height = height;
        this.parameters = parameters;
        this.interpolation = interpolation;
        this.fadeFunction = fadeFunction;
        this.colorCalculator = colorCalculator;
        this.generatorFactory = generatorFactory;
        this.noiseGeneratorFactory = noiseGeneratorFactory;
        this.starFieldApplicator = starFieldApplicator;
    }

    public BufferedImage create(long seed) {
        log.info("Creating galaxy image {}x{} with seed {} type {} multiLayer={}",
                width, height, seed, parameters.getGalaxyType(), parameters.isMultiLayerNoiseEnabled());

        PerlinGenerator noiseGenerator = noiseGeneratorFactory.createNoiseGenerator(
                parameters, seed, width, height, interpolation, fadeFunction);

        GalaxyIntensityCalculator intensityCalculator = createIntensityCalculator(noiseGenerator, seed);

        DomainWarpCalculator warpCalculator = createWarpCalculatorIfEnabled(seed);

        BufferedImage galaxyImage = buildImage(intensityCalculator, warpCalculator);

        return starFieldApplicator.applyIfEnabled(galaxyImage, parameters, seed);
    }

    private DomainWarpCalculator createWarpCalculatorIfEnabled(long seed) {
        if (parameters.getWarpStrength() <= 0.0) {
            return null;
        }

        return new DomainWarpCalculator(
                width,
                height,
                parameters.getWarpStrength(),
                seed,
                interpolation,
                fadeFunction);
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
                    .clusterCount(parameters.getClusterCount() != null ? parameters.getClusterCount()
                            : GalaxyDefaults.DEFAULT_CLUSTER_COUNT)
                    .clusterSize(parameters.getClusterSize() != null ? parameters.getClusterSize()
                            : GalaxyDefaults.DEFAULT_CLUSTER_SIZE)
                    .clusterConcentration(
                            parameters.getClusterConcentration() != null ? parameters.getClusterConcentration()
                                    : GalaxyDefaults.DEFAULT_CLUSTER_CONCENTRATION)
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
                    .sersicIndex(parameters.getSersicIndex() != null ? parameters.getSersicIndex()
                            : GalaxyDefaults.DEFAULT_SERSIC_INDEX)
                    .axisRatio(parameters.getAxisRatio() != null ? parameters.getAxisRatio()
                            : GalaxyDefaults.DEFAULT_AXIS_RATIO)
                    .orientationAngle(parameters.getOrientationAngle() != null ? parameters.getOrientationAngle()
                            : GalaxyDefaults.DEFAULT_ORIENTATION_ANGLE)
                    .build();
            case RING -> RingGalaxyGenerator.builder()
                    .width(width)
                    .height(height)
                    .noiseGenerator(noiseGenerator)
                    .coreSize(parameters.getCoreSize())
                    .galaxyRadius(parameters.getGalaxyRadius())
                    .ringRadius(parameters.getRingRadius() != null ? parameters.getRingRadius()
                            : GalaxyDefaults.DEFAULT_RING_RADIUS)
                    .ringWidth(parameters.getRingWidth() != null ? parameters.getRingWidth()
                            : GalaxyDefaults.DEFAULT_RING_WIDTH)
                    .ringIntensity(parameters.getRingIntensity() != null ? parameters.getRingIntensity()
                            : GalaxyDefaults.DEFAULT_RING_INTENSITY)
                    .coreToRingRatio(parameters.getCoreToRingRatio() != null ? parameters.getCoreToRingRatio()
                            : GalaxyDefaults.DEFAULT_CORE_TO_RING_RATIO)
                    .build();
            case IRREGULAR -> IrregularGalaxyGenerator.builder()
                    .width(width)
                    .height(height)
                    .noiseGenerator(noiseGenerator)
                    .seed(seed)
                    .coreSize(parameters.getCoreSize())
                    .galaxyRadius(parameters.getGalaxyRadius())
                    .irregularity(parameters.getIrregularity() != null ? parameters.getIrregularity()
                            : GalaxyDefaults.DEFAULT_IRREGULARITY)
                    .clumpCount(parameters.getIrregularClumpCount() != null ? parameters.getIrregularClumpCount()
                            : GalaxyDefaults.DEFAULT_CLUMP_COUNT)
                    .clumpSize(parameters.getIrregularClumpSize() != null ? parameters.getIrregularClumpSize()
                            : GalaxyDefaults.DEFAULT_CLUMP_SIZE)
                    .build();
        };
    }

    private BufferedImage buildImage(GalaxyIntensityCalculator intensityCalculator,
            DomainWarpCalculator warpCalculator) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setBackground(colorCalculator.getSpaceBackgroundColor());
        g2d.clearRect(0, 0, width, height);
        g2d.dispose();

        IntStream.range(0, width).parallel().forEach(x -> {
            for (int y = 0; y < height; y++) {
                double galaxyIntensity;

                // Apply domain warping if enabled
                if (warpCalculator != null && warpCalculator.isEnabled()) {
                    double[] warpedCoords = warpCalculator.warpCoordinates(x, y);
                    int warpedX = (int) Math.round(warpedCoords[0]);
                    int warpedY = (int) Math.round(warpedCoords[1]);

                    // Clamp to image bounds
                    warpedX = Math.max(0, Math.min(width - 1, warpedX));
                    warpedY = Math.max(0, Math.min(height - 1, warpedY));

                    galaxyIntensity = intensityCalculator.calculateGalaxyIntensity(warpedX, warpedY);
                } else {
                    galaxyIntensity = intensityCalculator.calculateGalaxyIntensity(x, y);
                }

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
        private GalaxyGeneratorFactory generatorFactory;
        private NoiseGeneratorFactory noiseGeneratorFactory;
        private StarFieldApplicator starFieldApplicator;

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

        public Builder withGeneratorFactory(GalaxyGeneratorFactory generatorFactory) {
            this.generatorFactory = generatorFactory;
            return this;
        }

        public Builder withNoiseGeneratorFactory(NoiseGeneratorFactory noiseGeneratorFactory) {
            this.noiseGeneratorFactory = noiseGeneratorFactory;
            return this;
        }

        public Builder withStarFieldApplicator(StarFieldApplicator starFieldApplicator) {
            this.starFieldApplicator = starFieldApplicator;
            return this;
        }

        public GalaxyImageCalculator build() {
            if (parameters == null) {
                throw new IllegalStateException("parameters must be set");
            }
            if (colorCalculator == null) {
                throw new IllegalStateException("colorCalculator must be set");
            }
            if (generatorFactory == null) {
                throw new IllegalStateException("generatorFactory must be set");
            }
            if (noiseGeneratorFactory == null) {
                throw new IllegalStateException("noiseGeneratorFactory must be set");
            }
            if (starFieldApplicator == null) {
                throw new IllegalStateException("starFieldApplicator must be set");
            }
            return new GalaxyImageCalculator(width, height, parameters, interpolation, fadeFunction, colorCalculator,
                    generatorFactory, noiseGeneratorFactory, starFieldApplicator);
        }
    }
}
