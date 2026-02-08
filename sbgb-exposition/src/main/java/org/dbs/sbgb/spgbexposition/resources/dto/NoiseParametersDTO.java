package org.dbs.sbgb.spgbexposition.resources.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoiseParametersDTO {
    private int octaves;
    private double persistence;
    private double lacunarity;
    private double scale;
}
