package org.dbs.sbgb.domain.model;

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

    // Irregular parameters
    private Double irregularity;
    private Integer irregularClumpCount;
    private Double irregularClumpSize;

    // Domain warping parameter (applicable to all galaxy types)
    private double warpStrength;

    // Color palette parameter
    private String colorPalette;

    // Star field parameters (applicable to all galaxy types)
    private double starDensity;
    private int maxStarSize;
    private boolean diffractionSpikes;
    private int spikeCount;

    // Multi-layer noise parameters (applicable to all galaxy types)
    private boolean multiLayerNoiseEnabled;
    private double macroLayerScale;
    private double macroLayerWeight;
    private double mesoLayerScale;
    private double mesoLayerWeight;
    private double microLayerScale;
    private double microLayerWeight;

    // Color parameters (legacy - used when colorPalette is null)
    private String spaceBackgroundColor;
    private String coreColor;
    private String armColor;
    private String outerColor;
}
