package org.dbs.sbgb.domain.model.parameters;

/**
 * Presets for Voronoi cluster galaxies using Java 21 Records.
 */
public record VoronoiPresets(
        String name,
        int clusterCount,
        double clusterSize,
        double clusterConcentration,
        double coreSize,
        double galaxyRadius) {
    public static final VoronoiPresets CLASSIC = new VoronoiPresets(
            "CLASSIC", 80, 60.0, 0.7, 0.05, 1500.0);

    public static final VoronoiPresets DENSE = new VoronoiPresets(
            "DENSE", 200, 40.0, 0.85, 0.08, 1500.0);

    public static final VoronoiPresets SPARSE = new VoronoiPresets(
            "SPARSE", 30, 90.0, 0.4, 0.03, 1500.0);
}
