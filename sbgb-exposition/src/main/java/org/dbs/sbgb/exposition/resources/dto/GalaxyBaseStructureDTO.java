package org.dbs.sbgb.exposition.resources.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GalaxyBaseStructureDTO {
    private UUID id;
    private String description;
    private int maxNote;
    private int width;
    private int height;
    private long seed;
    private String galaxyType;
    private double coreSize;
    private double galaxyRadius;
    private double warpStrength;
    private int noiseOctaves;
    private double noisePersistence;
    private double noiseLacunarity;
    private double noiseScale;
    private boolean multiLayerEnabled;
    private double macroLayerScale;
    private double macroLayerWeight;
    private double mesoLayerScale;
    private double mesoLayerWeight;
    private double microLayerScale;
    private double microLayerWeight;
    private String structureParams;
    // Spiral
    private Integer numberOfArms;
    private Double armWidth;
    private Double armRotation;
    private Double darkLaneOpacity;
    // Voronoi
    private Integer clusterCount;
    private Double clusterSize;
    private Double clusterConcentration;
    // Elliptical
    private Double sersicIndex;
    private Double axisRatio;
    private Double orientationAngle;
    // Ring
    private Double ringRadius;
    private Double ringWidth;
    private Double ringIntensity;
    private Double coreToRingRatio;
    // Irregular
    private Double irregularity;
    private Integer irregularClumpCount;
    private Double irregularClumpSize;
}
