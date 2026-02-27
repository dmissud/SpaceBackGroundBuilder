package org.dbs.sbgb.domain.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Applique les grilles de bruit pré-calculées avec leur configuration de blending
 * pour produire une image multi-couches.
 */
public class MultiLayerRenderer {

    private final int width;
    private final int height;
    private final NoiseColorCalculator colorCalculator;

    public MultiLayerRenderer(int width, int height, NoiseColorCalculator colorCalculator) {
        this.width = width;
        this.height = height;
        this.colorCalculator = colorCalculator;
    }

    /** Compose les grilles et leurs configs de layer en une image finale. */
    public BufferedImage renderLayers(List<NormalizedNoiseGrid> grids, List<LayerConfig> layerConfigs) {
        List<LayerConfig> enabledLayers = layerConfigs.stream().filter(LayerConfig::isEnabled).toList();

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setBackground(colorCalculator.getBackGroundColor());
        g2d.clearRect(0, 0, width, height);
        g2d.dispose();

        IntStream.range(0, width).parallel().forEach(x -> {
            for (int y = 0; y < height; y++) {
                img.setRGB(x, y, blendLayers(x, y, grids, enabledLayers).getRGB());
            }
        });

        return img;
    }

    private Color blendLayers(int x, int y, List<NormalizedNoiseGrid> grids, List<LayerConfig> enabledLayers) {
        if (grids.isEmpty()) return colorCalculator.getBackGroundColor();

        Color baseColor = colorCalculator.calculateNoiseColor(grids.get(0).normalizedValueAt(x, y));

        for (int i = 1; i < grids.size() && i < enabledLayers.size(); i++) {
            LayerConfig layer = enabledLayers.get(i);
            Color layerColor = colorCalculator.calculateNoiseColor(grids.get(i).normalizedValueAt(x, y));
            baseColor = blendColors(baseColor, layerColor, layer);
        }

        return baseColor;
    }

    private Color blendColors(Color base, Color overlay, LayerConfig layer) {
        int r = LayerBlender.blendColorComponent(base.getRed(), overlay.getRed(), layer.getBlendMode(), layer.getOpacity());
        int g = LayerBlender.blendColorComponent(base.getGreen(), overlay.getGreen(), layer.getBlendMode(), layer.getOpacity());
        int b = LayerBlender.blendColorComponent(base.getBlue(), overlay.getBlue(), layer.getBlendMode(), layer.getOpacity());
        int a = Math.max(base.getAlpha(), overlay.getAlpha());
        return new Color(r, g, b, a);
    }
}
