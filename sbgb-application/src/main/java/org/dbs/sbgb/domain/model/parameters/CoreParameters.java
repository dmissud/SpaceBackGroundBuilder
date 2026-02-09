package org.dbs.sbgb.domain.model.parameters;

import lombok.Builder;
import lombok.Value;

/**
 * Core parameters common to all galaxy types.
 * Defines the central bulge and overall size of the galaxy.
 */
@Value
@Builder
public class CoreParameters {

    /**
     * Relative size of the core (0.0-1.0)
     * Smaller values = tighter core, larger values = diffuse core
     */
    @Builder.Default
    double coreSize = 0.05;

    /**
     * Total radius of the galaxy in pixels
     */
    @Builder.Default
    double galaxyRadius = 1500.0;
}
