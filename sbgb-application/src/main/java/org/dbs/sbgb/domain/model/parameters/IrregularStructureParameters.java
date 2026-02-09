package org.dbs.sbgb.domain.model.parameters;

import lombok.Builder;
import lombok.Value;

/**
 * Structure parameters specific to irregular galaxies.
 * Defines the chaotic, asymmetric structure characteristic.
 */
@Value
@Builder
public class IrregularStructureParameters {

    /**
     * Irregularity factor (0.0-1.0, higher = more chaotic)
     */
    @Builder.Default
    double irregularity = 0.8;

    /**
     * Number of star-forming clumps
     */
    @Builder.Default
    int clumpCount = 15;

    /**
     * Average size of each clump in pixels
     */
    @Builder.Default
    double clumpSize = 80.0;
}
