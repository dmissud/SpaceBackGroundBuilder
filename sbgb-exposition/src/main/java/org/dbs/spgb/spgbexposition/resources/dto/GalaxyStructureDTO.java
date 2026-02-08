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
    private String galaxyType;
    private double coreSize;
    private double galaxyRadius;
    private double warpStrength;

    private NoiseParametersDTO noiseParameters;
    private SpiralParametersDTO spiralParameters;
    private VoronoiParametersDTO voronoiParameters;
    private EllipticalParametersDTO ellipticalParameters;
    private RingParametersDTO ringParameters;
    private IrregularParametersDTO irregularParameters;
    private StarFieldParametersDTO starFieldParameters;
    private MultiLayerNoiseParametersDTO multiLayerNoiseParameters;
    private ColorParametersDTO colorParameters;
}
