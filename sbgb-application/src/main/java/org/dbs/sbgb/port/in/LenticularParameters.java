package org.dbs.sbgb.port.in;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;

@Builder
public record LenticularParameters(
        @DecimalMin("0.5") @DecimalMax("10.0") Double sersicIndex,
        @DecimalMin("0.1") @DecimalMax("1.0") Double axisRatio,
        @DecimalMin("0.0") @DecimalMax("360.0") Double orientationAngle,
        @DecimalMin("0.0") @DecimalMax("1.0") Double diskContribution
) {
}
