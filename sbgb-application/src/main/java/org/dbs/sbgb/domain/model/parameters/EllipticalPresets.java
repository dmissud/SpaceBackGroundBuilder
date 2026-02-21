package org.dbs.sbgb.domain.model.parameters;

/**
 * Presets for elliptical galaxies using Java 21 Records.
 */
public record EllipticalPresets(
        String name,
        double sersicIndex,
        double axisRatio,
        double orientationAngle,
        double coreSize,
        double galaxyRadius) {
    public static final EllipticalPresets CLASSIC = new EllipticalPresets(
            "CLASSIC", 4.0, 0.7, 45.0, 0.05, 1500.0);

    public static final EllipticalPresets ROUND = new EllipticalPresets(
            "ROUND", 2.0, 0.95, 0.0, 0.08, 1500.0);

    public static final EllipticalPresets FLAT = new EllipticalPresets(
            "FLAT", 6.0, 0.4, 30.0, 0.04, 1500.0);
}
