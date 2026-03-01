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
}
