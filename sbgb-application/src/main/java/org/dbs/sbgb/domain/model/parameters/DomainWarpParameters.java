package org.dbs.sbgb.domain.model.parameters;

import lombok.Builder;
import lombok.Value;

/**
 * Domain warping parameters applicable to all galaxy types.
 * Creates organic distortions by warping coordinate space.
 */
@Value
@Builder
public class DomainWarpParameters {

    /**
     * Strength of domain warping (0.0 = disabled, 100-300 = typical)
     * Higher values create more pronounced filamentary structures
     */
    @Builder.Default
    double warpStrength = 0.0;

    /**
     * Check if domain warping is enabled
     */
    public boolean isEnabled() {
        return warpStrength > 0.0;
    }
}
