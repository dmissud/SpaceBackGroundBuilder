package org.dbs.sbgb.exposition.resources.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class NoiseBaseStructureDTO {
    private UUID id;
    private String description;
    private int maxNote;
    private int width;
    private int height;
    private int seed;
    private int octaves;
    private double persistence;
    private double lacunarity;
    private double scale;
    private String noiseType;
    private boolean useMultiLayer;
    private String layersConfig;
}
