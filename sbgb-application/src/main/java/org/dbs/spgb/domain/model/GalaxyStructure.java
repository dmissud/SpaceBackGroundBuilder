package org.dbs.spgb.domain.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GalaxyStructure {

    private int width;
    private int height;
    private long seed;
    private String galaxyType;

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

    // Voronoi cluster parameters
    private Integer clusterCount;
    private Double clusterSize;
    private Double clusterConcentration;

    // Elliptical parameters
    private Double sersicIndex;
    private Double axisRatio;
    private Double orientationAngle;

    // Ring parameters
    private Double ringRadius;
    private Double ringWidth;
    private Double ringIntensity;
    private Double coreToRingRatio;

    // Color parameters
    private String spaceBackgroundColor;
    private String coreColor;
    private String armColor;
    private String outerColor;
}
