package org.dbs.spgb.spgbexposition.resources.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GalaxyStructureDTO {
    private int width;
    private int height;
    private long seed;

    // Spiral structure parameters
    private int numberOfArms;
    private double armWidth;
    private double armRotation;
    private double coreSize;
    private double galaxyRadius;

    // Noise texture parameters
    private int noiseOctaves;
    private double noisePersistence;
    private double noiseLacunarity;
    private double noiseScale;
}
