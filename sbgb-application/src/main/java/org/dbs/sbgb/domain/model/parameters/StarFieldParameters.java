package org.dbs.sbgb.domain.model.parameters;

import lombok.Builder;
import lombok.Value;

/**
 * Star field parameters applicable to all galaxy types.
 * Adds a layer of stars on top of the galaxy.
 */
@Value
@Builder
public class StarFieldParameters {

    /**
     * Density of stars (0.0-0.01, typical 0.001)
     * Number of stars = density * width * height
     */
    @Builder.Default
    double starDensity = 0.0;

    /**
     * Maximum size of stars in pixels (1-10)
     */
    @Builder.Default
    int maxStarSize = 4;

    /**
     * Whether to add diffraction spikes to bright stars
     */
    @Builder.Default
    boolean diffractionSpikes = false;

    /**
     * Number of diffraction spike branches (4, 6, or 8)
     */
    @Builder.Default
    int spikeCount = 4;

    /**
     * Check if star field is enabled
     */
    public boolean isEnabled() {
        return starDensity > 0.0;
    }
}
