package org.dbs.spgb.port.in;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record NoiseParameters(
        @Min(1) @Max(10) int octaves,
        @DecimalMin("0.0") @DecimalMax("1.0") double persistence,
        @DecimalMin("1.0") double lacunarity,
        @DecimalMin("10.0") double scale
) {
    public static NoiseParameters defaultNoise() {
        return NoiseParameters.builder()
                .octaves(4)
                .persistence(0.5)
                .lacunarity(2.0)
                .scale(200.0)
                .build();
    }
}
