package org.dbs.spgb.domain.model;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Advanced color calculator using gradient with multiple color stops
 * Provides smooth color transitions across the full intensity range
 */
@Slf4j
public class GradientGalaxyColorCalculator implements GalaxyColorCalculator {

    private final List<ColorStop> colorStops;
    private final Color spaceBackground;

    /**
     * Create gradient calculator with custom color stops
     * @param colorStops List of color stops, must be sorted by position
     */
    public GradientGalaxyColorCalculator(List<ColorStop> colorStops) {
        if (colorStops == null || colorStops.size() < 2) {
            throw new IllegalArgumentException("At least 2 color stops required");
        }

        // Sort by position to ensure proper gradient
        this.colorStops = colorStops.stream()
            .sorted(Comparator.comparingDouble(ColorStop::getPosition))
            .toList();

        // Verify first stop is at 0.0 and last at 1.0
        if (this.colorStops.get(0).getPosition() != 0.0) {
            log.warn("First color stop is not at 0.0, adding implicit stop");
        }
        if (this.colorStops.get(this.colorStops.size() - 1).getPosition() != 1.0) {
            log.warn("Last color stop is not at 1.0, adding implicit stop");
        }

        this.spaceBackground = this.colorStops.get(0).getColor();
    }

    /**
     * Create gradient calculator with varargs color stops
     */
    public GradientGalaxyColorCalculator(ColorStop... colorStops) {
        this(Arrays.asList(colorStops));
    }

    @Override
    public Color calculateGalaxyColor(double intensity) {
        // Clamp intensity to [0, 1]
        intensity = Math.clamp(intensity, 0.0, 1.0);

        // Find the two color stops surrounding this intensity
        ColorStop lowerStop = colorStops.get(0);
        ColorStop upperStop = colorStops.get(colorStops.size() - 1);

        for (int i = 0; i < colorStops.size() - 1; i++) {
            ColorStop current = colorStops.get(i);
            ColorStop next = colorStops.get(i + 1);

            if (intensity >= current.getPosition() && intensity <= next.getPosition()) {
                lowerStop = current;
                upperStop = next;
                break;
            }
        }

        // Calculate interpolation factor between the two stops
        double range = upperStop.getPosition() - lowerStop.getPosition();
        if (range < 0.0001) {
            // Stops are at the same position, return lower color
            return lowerStop.getColor();
        }

        double t = (intensity - lowerStop.getPosition()) / range;

        // Apply smoothstep for natural transitions
        t = smoothstep(t);

        // Interpolate between the two colors
        return blendColors(lowerStop.getColor(), upperStop.getColor(), t);
    }

    @Override
    public Color getSpaceBackgroundColor() {
        return spaceBackground;
    }

    /**
     * Blend two colors with linear interpolation
     */
    private Color blendColors(Color color1, Color color2, double blend) {
        int r = (int) (color1.getRed() * (1 - blend) + color2.getRed() * blend);
        int g = (int) (color1.getGreen() * (1 - blend) + color2.getGreen() * blend);
        int b = (int) (color1.getBlue() * (1 - blend) + color2.getBlue() * blend);

        return new Color(
            Math.clamp(r, 0, 255),
            Math.clamp(g, 0, 255),
            Math.clamp(b, 0, 255)
        );
    }

    /**
     * Smoothstep function for natural color transitions
     */
    private double smoothstep(double t) {
        return t * t * (3.0 - 2.0 * t);
    }
}
