package org.dbs.spgb.port.in;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;

@Builder
public record MultiLayerNoiseParameters(
        boolean enabled,
        @DecimalMin("0.1") @DecimalMax("5.0") double macroLayerScale,
        @DecimalMin("0.0") @DecimalMax("1.0") double macroLayerWeight,
        @DecimalMin("0.1") @DecimalMax("5.0") double mesoLayerScale,
        @DecimalMin("0.0") @DecimalMax("1.0") double mesoLayerWeight,
        @DecimalMin("0.1") @DecimalMax("10.0") double microLayerScale,
        @DecimalMin("0.0") @DecimalMax("1.0") double microLayerWeight
) {
    public static MultiLayerNoiseParameters disabled() {
        return MultiLayerNoiseParameters.builder()
                .enabled(false)
                .macroLayerScale(0.3)
                .macroLayerWeight(0.5)
                .mesoLayerScale(1.0)
                .mesoLayerWeight(0.35)
                .microLayerScale(3.0)
                .microLayerWeight(0.15)
                .build();
    }
}
