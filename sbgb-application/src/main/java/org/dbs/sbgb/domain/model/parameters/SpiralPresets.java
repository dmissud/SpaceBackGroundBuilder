package org.dbs.sbgb.domain.model.parameters;

/**
 * Presets for spiral galaxies using Java 21 Records.
 */
public record SpiralPresets(
        String name,
        int numberOfArms,
        double armWidth,
        double armRotation,
        double coreSize,
        double galaxyRadius,
        double darkLaneOpacity) {
    public static final SpiralPresets CLASSIC = new SpiralPresets(
            "CLASSIC", 2, 80.0, 4.0, 0.05, 1500.0, 0.4);

    public static final SpiralPresets BARRED = new SpiralPresets(
            "BARRED", 2, 100.0, 1.5, 0.08, 1500.0, 0.2);

    public static final SpiralPresets MULTI_ARM = new SpiralPresets(
            "MULTI_ARM", 4, 50.0, 5.5, 0.04, 1600.0, 0.3);

    public static final SpiralPresets DUSTY_SPIRAL = new SpiralPresets(
            "DUSTY_SPIRAL", 2, 70.0, 3.5, 0.06, 1500.0, 0.8);
}
