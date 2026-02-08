package org.dbs.spgb.port.in;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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

    // Spiral structure parameters (nullable - only required when galaxyType == SPIRAL)
    @Min(1)
    @Max(10)
    private Integer numberOfArms;

    @DecimalMin("10.0")
    private Double armWidth;

    @DecimalMin("1.0")
    private Double armRotation;

    @DecimalMin("0.01")
    @DecimalMax("0.5")
    private Double coreSize;

    @DecimalMin("100.0")
    private Double galaxyRadius;

    // Noise texture parameters
    @Min(1)
    @Max(10)
    private int noiseOctaves;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private double noisePersistence;

    @DecimalMin("1.0")
    private double noiseLacunarity;

    @DecimalMin("10.0")
    private double noiseScale;

    // Voronoi cluster parameters (optional - only used when galaxyType == VORONOI_CLUSTER)
    @Min(5)
    @Max(500)
    private Integer clusterCount;

    @DecimalMin("10.0")
    private Double clusterSize;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private Double clusterConcentration;

    // Elliptical parameters (optional - only used when galaxyType == ELLIPTICAL)
    @DecimalMin("0.5")
    @DecimalMax("10.0")
    private Double sersicIndex;

    @DecimalMin("0.1")
    @DecimalMax("1.0")
    private Double axisRatio;

    @DecimalMin("0.0")
    @DecimalMax("360.0")
    private Double orientationAngle;

    // Ring parameters (optional - only used when galaxyType == RING)
    @DecimalMin("50.0")
    private Double ringRadius;

    @DecimalMin("10.0")
    private Double ringWidth;

    @DecimalMin("0.1")
    @DecimalMax("2.0")
    private Double ringIntensity;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private Double coreToRingRatio;

    // Irregular parameters (optional - only used when galaxyType == IRREGULAR)
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private Double irregularity;

    @Min(5)
    @Max(50)
    private Integer irregularClumpCount;

    @DecimalMin("20.0")
    private Double irregularClumpSize;

    // Domain warping parameter (optional - applicable to ALL galaxy types)
    @DecimalMin("0.0")
    @DecimalMax("300.0")
    @Builder.Default
    private double warpStrength = 0.0;

    // Color palette parameter (optional - defaults to CLASSIC if not provided)
    @Pattern(regexp = "NEBULA|CLASSIC|WARM|COLD|INFRARED|EMERALD")
    @Builder.Default
    private String colorPalette = "CLASSIC";

    // Color parameters (optional - defaults will be used if not provided)
    @Pattern(regexp = "^#([a-fA-F0-9]{6})$")
    @Builder.Default
    private String spaceBackgroundColor = "#050510"; // Very dark blue-black

    @Pattern(regexp = "^#([a-fA-F0-9]{6})$")
    @Builder.Default
    private String coreColor = "#FFFADC"; // Bright warm white

    @Pattern(regexp = "^#([a-fA-F0-9]{6})$")
    @Builder.Default
    private String armColor = "#B4C8FF"; // Light blue

    @Pattern(regexp = "^#([a-fA-F0-9]{6})$")
    @Builder.Default
    private String outerColor = "#3C5078"; // Dim blue
}
