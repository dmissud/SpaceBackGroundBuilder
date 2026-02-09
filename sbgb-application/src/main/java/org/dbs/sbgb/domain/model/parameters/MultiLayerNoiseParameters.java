package org.dbs.sbgb.domain.model.parameters;

import lombok.Builder;
import lombok.Value;

/**
 * Multi-layer noise parameters applicable to all galaxy types.
 * Creates richer texture by combining multiple noise scales.
 */
@Value
@Builder
public class MultiLayerNoiseParameters {

    /**
     * Whether multi-layer noise is enabled (vs simple Perlin)
     */
    @Builder.Default
    boolean enabled = false;

    /**
     * Macro layer scale multiplier (large-scale structures)
     */
    @Builder.Default
    double macroLayerScale = 0.3;

    /**
     * Macro layer contribution weight (0.0-1.0)
     */
    @Builder.Default
    double macroLayerWeight = 0.5;

    /**
     * Meso layer scale multiplier (medium-scale structures)
     */
    @Builder.Default
    double mesoLayerScale = 1.0;

    /**
     * Meso layer contribution weight (0.0-1.0)
     */
    @Builder.Default
    double mesoLayerWeight = 0.35;

    /**
     * Micro layer scale multiplier (fine-scale details)
     */
    @Builder.Default
    double microLayerScale = 3.0;

    /**
     * Micro layer contribution weight (0.0-1.0)
     */
    @Builder.Default
    double microLayerWeight = 0.15;
}
