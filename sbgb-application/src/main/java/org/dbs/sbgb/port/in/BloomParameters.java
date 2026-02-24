package org.dbs.sbgb.port.in;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record BloomParameters(
        boolean enabled,
        @Min(1) @Max(50) int bloomRadius,
        @DecimalMin("0.0") @DecimalMax("1.0") double bloomIntensity,
        @DecimalMin("0.0") @DecimalMax("1.0") double bloomThreshold) {

    public static BloomParameters disabled() {
        return BloomParameters.builder()
                .enabled(false)
                .bloomRadius(10)
                .bloomIntensity(0.5)
                .bloomThreshold(0.5)
                .build();
    }
}
