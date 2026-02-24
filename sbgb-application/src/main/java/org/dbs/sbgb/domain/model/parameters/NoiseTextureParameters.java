package org.dbs.sbgb.domain.model.parameters;

import lombok.Builder;
import lombok.Value;

/**
 * Noise texture parameters for Perlin noise generation.
 * Controls the organic texture applied to all galaxy types.
 */
@Value
@Builder
public class NoiseTextureParameters {

    /**
     * Number of octaves in the Perlin noise (more = more detail)
     */
    @Builder.Default
    int octaves = 4;

    /**
     * Persistence factor (amplitude reduction per octave, 0.0-1.0)
     */
    @Builder.Default
    double persistence = 0.5;

    /**
     * Lacunarity (frequency multiplier per octave, typically ~2.0)
     */
    @Builder.Default
    double lacunarity = 2.0;

    /**
     * Base scale of the noise pattern in pixels
     */
    @Builder.Default
    double scale = 200.0;
}
