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
}
