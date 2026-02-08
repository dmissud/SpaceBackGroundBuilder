package org.dbs.spgb.port.in;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;

@Builder
public record RingParameters(
        @DecimalMin("50.0") Double ringRadius,
        @DecimalMin("10.0") Double ringWidth,
        @DecimalMin("0.1") @DecimalMax("2.0") Double ringIntensity,
        @DecimalMin("0.0") @DecimalMax("1.0") Double coreToRingRatio
) {
}
