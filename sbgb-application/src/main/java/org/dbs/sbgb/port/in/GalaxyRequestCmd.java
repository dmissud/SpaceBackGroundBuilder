package org.dbs.sbgb.port.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GalaxyRequestCmd {
    private String name;
    private String description;

    @Min(100)
    @Max(4000)
    private int width;

    @Min(100)
    @Max(4000)
    private int height;

    private long seed;

    private boolean forceUpdate;

    @Builder.Default
    private String galaxyType = "SPIRAL";

    @DecimalMin("0.01")
    @DecimalMax("0.5")
    private Double coreSize;

    @DecimalMin("100.0")
    private Double galaxyRadius;

    @Builder.Default
    @DecimalMin("0.0")
    @DecimalMax("300.0")
    private double warpStrength = 0.0;

    @Valid
    @Builder.Default
    private NoiseParameters noiseParameters = NoiseParameters.defaultNoise();

    @Valid
    private SpiralParameters spiralParameters;

    @Valid
    private VoronoiParameters voronoiParameters;

    @Valid
    private EllipticalParameters ellipticalParameters;

    @Valid
    private RingParameters ringParameters;

    @Valid
    private IrregularParameters irregularParameters;

    @Valid
    @Builder.Default
    private StarFieldParameters starFieldParameters = StarFieldParameters.noStars();

    @Valid
    @Builder.Default
    private MultiLayerNoiseParameters multiLayerNoiseParameters = MultiLayerNoiseParameters.disabled();

    @Valid
    @Builder.Default
    private ColorParameters colorParameters = ColorParameters.classicPalette();
}
