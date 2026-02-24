package org.dbs.sbgb.port.in;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record VoronoiParameters(
        @Min(5) @Max(500) Integer clusterCount,
        @DecimalMin("10.0") Double clusterSize,
        @DecimalMin("0.0") @DecimalMax("1.0") Double clusterConcentration
) {
}
