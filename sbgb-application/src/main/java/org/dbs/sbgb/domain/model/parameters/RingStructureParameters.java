package org.dbs.sbgb.domain.model.parameters;

import lombok.Builder;
import lombok.Value;

/**
 * Structure parameters specific to ring galaxies (Hoag's Object style).
 * Defines the ring configuration and core-to-ring relationship.
 */
@Value
@Builder
public class RingStructureParameters {

    /**
     * Radius of the ring from galaxy center in pixels
     */
    @Builder.Default
    double ringRadius = 900.0;

    /**
     * Width (thickness) of the ring in pixels
     */
    @Builder.Default
    double ringWidth = 150.0;

    /**
     * Intensity multiplier for the ring (1.0 = standard)
     */
    @Builder.Default
    double ringIntensity = 1.0;

    /**
     * Ratio of core brightness to ring brightness (0.0-1.0)
     */
    @Builder.Default
    double coreToRingRatio = 0.3;
}
