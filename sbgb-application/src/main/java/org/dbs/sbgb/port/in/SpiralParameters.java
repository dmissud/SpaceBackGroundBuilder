package org.dbs.sbgb.port.in;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record SpiralParameters(
                @Min(1) @Max(10) Integer numberOfArms,
                @DecimalMin("10.0") Double armWidth,
                @DecimalMin("1.0") Double armRotation,
                @DecimalMin("0.0") @DecimalMax("1.0") Double darkLaneOpacity) {
}
