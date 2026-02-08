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

    @Builder.Default
    private String galaxyType = "SPIRAL";

    // Spiral structure parameters
    @Min(1)
    @Max(10)
    private int numberOfArms;

    @DecimalMin("10.0")
    private double armWidth;

    @DecimalMin("1.0")
    private double armRotation;

    @DecimalMin("0.01")
    @DecimalMax("0.5")
    private double coreSize;

    @DecimalMin("100.0")
    private double galaxyRadius;

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
