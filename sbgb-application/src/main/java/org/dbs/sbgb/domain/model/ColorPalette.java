package org.dbs.sbgb.domain.model;

import java.util.List;

/**
 * Predefined color palettes for galaxy rendering
 * Each palette provides a different visual style
 */
public enum ColorPalette {

    /**
     * Nebula-style palette with purple, magenta, cyan and turquoise
     * Rich, vibrant colors typical of emission nebulae
     */
    NEBULA(
        new ColorStop(0.00, 5, 5, 15),         // Deep black
        new ColorStop(0.05, 40, 10, 60),       // Dark violet
        new ColorStop(0.15, 120, 30, 100),     // Magenta
        new ColorStop(0.30, 30, 80, 130),      // Dark cyan
        new ColorStop(0.50, 60, 180, 200),     // Turquoise
        new ColorStop(0.70, 130, 220, 255),    // Light cyan
        new ColorStop(0.85, 240, 230, 200),    // Warm white
        new ColorStop(1.00, 255, 255, 255)     // Bright white
    ),

    /**
     * Classic blue-white palette (similar to DefaultGalaxyColorCalculator)
     * Traditional galaxy appearance
     */
    CLASSIC(
        new ColorStop(0.00, 5, 5, 15),         // Very dark blue-black
        new ColorStop(0.01, 20, 25, 40),       // Dark blue
        new ColorStop(0.30, 60, 80, 120),      // Dim blue
        new ColorStop(0.70, 180, 200, 255),    // Light blue
        new ColorStop(1.00, 255, 250, 220)     // Bright warm white
    ),

    /**
     * Warm palette with orange, yellow and red tones
     * Suitable for old, red galaxies
     */
    WARM(
        new ColorStop(0.00, 10, 5, 5),         // Deep red-black
        new ColorStop(0.10, 60, 20, 10),       // Dark red
        new ColorStop(0.30, 140, 50, 20),      // Orange-red
        new ColorStop(0.50, 200, 100, 40),     // Orange
        new ColorStop(0.70, 255, 180, 80),     // Yellow-orange
        new ColorStop(0.85, 255, 230, 150),    // Light yellow
        new ColorStop(1.00, 255, 250, 240)     // Warm white
    ),

    /**
     * Cold palette with blue and cyan tones
     * Suitable for young, blue galaxies with active star formation
     */
    COLD(
        new ColorStop(0.00, 2, 5, 15),         // Very dark blue-black
        new ColorStop(0.10, 10, 30, 80),       // Deep blue
        new ColorStop(0.30, 20, 80, 150),      // Blue
        new ColorStop(0.50, 40, 140, 200),     // Light blue
        new ColorStop(0.70, 100, 200, 240),    // Cyan
        new ColorStop(0.85, 180, 230, 255),    // Light cyan
        new ColorStop(1.00, 240, 250, 255)     // Blue-white
    ),

    /**
     * Infrared-style palette simulating infrared telescope imagery
     * Red and yellow tones revealing dust and cooler regions
     */
    INFRARED(
        new ColorStop(0.00, 0, 0, 5),          // Near black
        new ColorStop(0.15, 40, 0, 10),        // Dark red
        new ColorStop(0.30, 100, 20, 20),      // Red
        new ColorStop(0.50, 180, 60, 30),      // Orange-red
        new ColorStop(0.70, 240, 140, 50),     // Orange
        new ColorStop(0.85, 255, 220, 100),    // Yellow
        new ColorStop(1.00, 255, 255, 200)     // Bright yellow-white
    ),

    /**
     * Green-tinted palette for exotic/alien appearance
     * Not physically realistic but visually striking
     */
    EMERALD(
        new ColorStop(0.00, 2, 10, 8),         // Very dark teal
        new ColorStop(0.10, 10, 40, 30),       // Dark green-cyan
        new ColorStop(0.30, 20, 100, 70),      // Teal
        new ColorStop(0.50, 40, 180, 120),     // Green-cyan
        new ColorStop(0.70, 100, 240, 180),    // Light green
        new ColorStop(0.85, 180, 255, 220),    // Pale green
        new ColorStop(1.00, 240, 255, 250)     // Green-white
    );

    private final List<ColorStop> colorStops;

    ColorPalette(ColorStop... stops) {
        this.colorStops = List.of(stops);
    }

    /**
     * Get the list of color stops for this palette
     */
    public List<ColorStop> getColorStops() {
        return colorStops;
    }

    /**
     * Create a GradientGalaxyColorCalculator with this palette
     */
    public GradientGalaxyColorCalculator createCalculator() {
        return new GradientGalaxyColorCalculator(colorStops);
    }
}
