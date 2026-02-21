package org.dbs.sbgb.domain.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ImageStructure {
    private int height;
    private int width;
    private int seed;
    private int octaves;
    private double persistence;
    private double lacunarity;
    private double scale;
    private String preset;
    private boolean useMultiLayer;
    private NoiseType noiseType;
}