package org.dbs.sbgb.domain.model;

import java.awt.*;

/**
 * Default color calculator for realistic galaxy appearance
 * Maps intensity to colors: dark blue/black -> yellow/white core -> blue arms
 */
public class DefaultGalaxyColorCalculator implements GalaxyColorCalculator {

    private final Color spaceBackground;
    private final Color coreColor;
    private final Color armColor;
    private final Color outerColor;

    public DefaultGalaxyColorCalculator() {
        this.spaceBackground = new Color(5, 5, 15); // Very dark blue-black
        this.coreColor = new Color(255, 250, 220); // Bright warm white
        this.armColor = new Color(180, 200, 255); // Light blue
        this.outerColor = new Color(60, 80, 120); // Dim blue
    }

    public DefaultGalaxyColorCalculator(Color spaceBackground, Color coreColor, Color armColor, Color outerColor) {
        this.spaceBackground = spaceBackground;
        this.coreColor = coreColor;
        this.armColor = armColor;
        this.outerColor = outerColor;
    }

    @Override
    public Color calculateGalaxyColor(double intensity) {
        if (intensity < 0.01) {
            return spaceBackground;
        }

        // Core region: very bright warm colors (intensity > 0.7)
        if (intensity > 0.7) {
            double coreBlend = (intensity - 0.7) / 0.3;
            return blendColors(armColor, coreColor, coreBlend);
        }

        // Arm region: blue-white colors (0.3 < intensity < 0.7)
        if (intensity > 0.3) {
            double armBlend = (intensity - 0.3) / 0.4;
            return blendColors(outerColor, armColor, armBlend);
        }

        // Outer region: dim to outer color (0.01 < intensity < 0.3)
        double outerBlend = intensity / 0.3;
        return blendColors(spaceBackground, outerColor, outerBlend);
    }

    @Override
    public Color getSpaceBackgroundColor() {
        return spaceBackground;
    }

    /**
     * Blend two colors with smooth interpolation
     */
    private Color blendColors(Color color1, Color color2, double blend) {
        // Apply smoothstep for more natural color transitions
        blend = smoothstep(Math.clamp(blend, 0.0, 1.0));

        int r = (int) (color1.getRed() * (1 - blend) + color2.getRed() * blend);
        int g = (int) (color1.getGreen() * (1 - blend) + color2.getGreen() * blend);
        int b = (int) (color1.getBlue() * (1 - blend) + color2.getBlue() * blend);

        return new Color(
            Math.clamp(r, 0, 255),
            Math.clamp(g, 0, 255),
            Math.clamp(b, 0, 255)
        );
    }

    private double smoothstep(double t) {
        return t * t * (3.0 - 2.0 * t);
    }
}
