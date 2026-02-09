package org.dbs.sbgb.domain.constant;

import org.dbs.sbgb.domain.model.GalaxyType;

public final class GalaxyDefaults {

    private GalaxyDefaults() {
        // Prevent instantiation
    }

    public static final GalaxyType DEFAULT_GALAXY_TYPE = GalaxyType.SPIRAL;
    public static final int DEFAULT_SPIRAL_ARMS = 2;
    public static final double DEFAULT_ARM_WIDTH = 80.0;
    public static final double DEFAULT_ARM_ROTATION = 4.0;
    public static final double DEFAULT_CORE_SIZE = 0.05;
    public static final double DEFAULT_GALAXY_RADIUS = 1500.0;

    // Voronoi
    public static final int DEFAULT_CLUSTER_COUNT = 80;
    public static final double DEFAULT_CLUSTER_SIZE = 60.0;
    public static final double DEFAULT_CLUSTER_CONCENTRATION = 0.7;

    // Elliptical
    public static final double DEFAULT_SERSIC_INDEX = 4.0;
    public static final double DEFAULT_AXIS_RATIO = 0.7;
    public static final double DEFAULT_ORIENTATION_ANGLE = 0.0;

    // Ring
    public static final double DEFAULT_RING_RADIUS = 900.0;
    public static final double DEFAULT_RING_WIDTH = 150.0;
    public static final double DEFAULT_RING_INTENSITY = 1.0;
    public static final double DEFAULT_CORE_TO_RING_RATIO = 0.3;

    // Irregular
    public static final double DEFAULT_IRREGULARITY = 0.8;
    public static final int DEFAULT_CLUMP_COUNT = 15;
    public static final double DEFAULT_CLUMP_SIZE = 80.0;
}
