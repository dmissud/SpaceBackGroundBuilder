package org.dbs.sbgb.port.in;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record StarFieldParameters(
        @DecimalMin("0.0") @DecimalMax("0.01") double density,
        @Min(1) @Max(10) int maxStarSize,
        boolean diffractionSpikes,
        @Min(4) @Max(8) int spikeCount
) {
    public static StarFieldParameters noStars() {
        return StarFieldParameters.builder()
                .density(0.0)
                .maxStarSize(4)
                .diffractionSpikes(false)
                .spikeCount(4)
                .build();
    }
}
