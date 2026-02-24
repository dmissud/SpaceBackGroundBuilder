package org.dbs.sbgb.domain.model.parameters;

import lombok.Builder;
import lombok.Value;

/**
 * Structure parameters specific to Voronoi cluster galaxies.
 * Defines the cluster distribution and characteristics.
 */
@Value
@Builder
public class VoronoiClusterParameters {

    /**
     * Number of cluster centers to generate
     */
    @Builder.Default
    int clusterCount = 80;

    /**
     * Size of each cluster in pixels
     */
    @Builder.Default
    double clusterSize = 60.0;

    /**
     * Concentration factor (0.0-1.0, higher = more concentrated toward center)
     */
    @Builder.Default
    double clusterConcentration = 0.7;
}
