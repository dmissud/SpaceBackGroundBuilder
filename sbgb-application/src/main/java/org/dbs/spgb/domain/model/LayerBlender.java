package org.dbs.spgb.domain.model;

public class LayerBlender {

    public static double blend(double base, double layer, BlendMode mode, double opacity) {
        layer = layer * opacity;

        return switch (mode) {
            case NORMAL -> (1 - opacity) * base + layer;
            case MULTIPLY -> base * layer;
            case SCREEN -> 1.0 - (1.0 - base) * (1.0 - layer);
            case OVERLAY -> base < 0.5
                ? 2.0 * base * layer
                : 1.0 - 2.0 * (1.0 - base) * (1.0 - layer);
            case ADD -> Math.min(1.0, base + layer);
        };
    }

    public static int blendColorComponent(int baseComponent, int layerComponent, BlendMode mode, double opacity) {
        double baseNormalized = baseComponent / 255.0;
        double layerNormalized = layerComponent / 255.0;

        double blended = blend(baseNormalized, layerNormalized, mode, opacity);

        return Math.clamp((int) (blended * 255.0), 0, 255);
    }
}
