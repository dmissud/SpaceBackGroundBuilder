package org.dbs.sbgb.domain.model.parameters;

import lombok.Builder;
import lombok.Value;

/**
 * Bloom/glow post-processing parameters.
 * Applies a Gaussian halo around bright pixels for realistic light bleed.
 */
@Value
@Builder
public class BloomParameters {

    @Builder.Default
    boolean enabled = false;

    /** Blur radius in pixels (1-50) */
    @Builder.Default
    int bloomRadius = 10;

    /** Additive intensity of the bloom layer (0.0-1.0) */
    @Builder.Default
    double bloomIntensity = 0.5;

    /** Luminance threshold above which pixels contribute to bloom (0.0-1.0) */
    @Builder.Default
    double bloomThreshold = 0.5;
}
