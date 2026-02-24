package org.dbs.sbgb.port.in;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record IrregularParameters(
        @DecimalMin("0.0") @DecimalMax("1.0") Double irregularity,
        @Min(5) @Max(50) Integer irregularClumpCount,
        @DecimalMin("20.0") Double irregularClumpSize
) {
}
