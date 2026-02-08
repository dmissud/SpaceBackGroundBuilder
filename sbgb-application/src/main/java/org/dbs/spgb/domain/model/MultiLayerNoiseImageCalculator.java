package org.dbs.spgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class MultiLayerNoiseImageCalculator {

    public static final int DEFAULT_IMAGE_WIDTH = 4000;
    public static final int DEFAULT_IMAGE_HEIGHT = 4000;
    public static final Interpolation DEFAULT_INTERPOLATION = Interpolation.COSINE;
    public static final FadeFunction DEFAULT_FADE_FUNCTION = FadeFunction.CUBIC_POLY;

    private final int width;
    private final int height;
    private final List<LayerConfig> layers;
    private final Interpolation interpolation;
    private final FadeFunction fadeFunction;
    private final NoiseColorCalculator noiseColorCalculator;
    private final List<PerlinGenerator> layerGenerators;

    private MultiLayerNoiseImageCalculator(int width,
                                           int height,
                                           List<LayerConfig> layers,
                                           Interpolation interpolation,
                                           FadeFunction fadeFunction,
                                           NoiseColorCalculator noiseColorCalculator) {
        this.width = width;
        this.height = height;
        this.layers = layers;
        this.interpolation = interpolation;
        this.fadeFunction = fadeFunction;
        this.noiseColorCalculator = noiseColorCalculator;
        this.layerGenerators = new ArrayList<>();
    }

    public BufferedImage create(long seed) {
        // Initialize generators for each enabled layer
        layerGenerators.clear();
        for (LayerConfig layer : layers) {
            if (layer.isEnabled()) {
                PerlinGenerator generator = new PerlinGenerator(interpolation, fadeFunction);
                generator.createNoisePipeline(
                        seed + layer.getSeedOffset(),
                        width,
                        height,
                        layer.getOctaves(),
                        layer.getPersistence(),
                        layer.getLacunarity(),
                        layer.getScale(),
                        layer.getNoiseType() != null ? layer.getNoiseType() : NoiseType.FBM
                );
                generator.performNormalization();
                layerGenerators.add(generator);
            }
        }

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
                Color finalColor = calculateLayeredColor(x, y);
                img.setRGB(x, y, finalColor.getRGB());
            }
        });

        return img;
    }

    private Color calculateLayeredColor(int x, int y) {
        // Start with base noise value from first layer
        if (layerGenerators.isEmpty()) {
            return noiseColorCalculator.getBackGroundColor();
        }

        double baseNoise = layerGenerators.get(0).scaleNoiseNormalizedValue(x, y);
        Color baseColor = noiseColorCalculator.calculateNoiseColor(baseNoise);

        // Blend additional layers
        int enabledLayerIndex = 0;
        for (int i = 0; i < layers.size(); i++) {
            LayerConfig layer = layers.get(i);
            if (!layer.isEnabled()) continue;

            if (enabledLayerIndex == 0) {
                // First layer is the base
                enabledLayerIndex++;
                continue;
            }

            PerlinGenerator layerGenerator = layerGenerators.get(enabledLayerIndex);
            double layerNoise = layerGenerator.scaleNoiseNormalizedValue(x, y);
            Color layerColor = noiseColorCalculator.calculateNoiseColor(layerNoise);

            // Blend layer with base
            int r = LayerBlender.blendColorComponent(
                    baseColor.getRed(),
                    layerColor.getRed(),
                    layer.getBlendMode(),
                    layer.getOpacity()
            );
            int g = LayerBlender.blendColorComponent(
                    baseColor.getGreen(),
                    layerColor.getGreen(),
                    layer.getBlendMode(),
                    layer.getOpacity()
            );
            int b = LayerBlender.blendColorComponent(
                    baseColor.getBlue(),
                    layerColor.getBlue(),
                    layer.getBlendMode(),
                    layer.getOpacity()
            );

            int a = Math.max(baseColor.getAlpha(), layerColor.getAlpha());
            baseColor = new Color(r, g, b, a);
            enabledLayerIndex++;
        }

        return baseColor;
    }

    public static class Builder {
        private int width;
        private int height;
        private List<LayerConfig> layers;
        private Interpolation interpolation;
        private FadeFunction fadeFunction;
        private NoiseColorCalculator noiseColorCalculator;

        public Builder() {
            this.width = DEFAULT_IMAGE_WIDTH;
            this.height = DEFAULT_IMAGE_HEIGHT;
            this.layers = new ArrayList<>();
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

        public Builder withLayers(List<LayerConfig> layers) {
            this.layers = layers;
            return this;
        }

        public Builder withPreset(ImagePreset preset) {
            this.layers = preset.getDefaultLayers();
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

        public Builder withNoiseColorCalculator(NoiseColorCalculator noiseColorCalculator) {
            this.noiseColorCalculator = noiseColorCalculator;
            return this;
        }

        public MultiLayerNoiseImageCalculator build() {
            return new MultiLayerNoiseImageCalculator(width, height, layers, interpolation, fadeFunction, noiseColorCalculator);
        }
    }
}
