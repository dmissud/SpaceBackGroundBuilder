package org.dbs.spgb.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LayerConfig {
    private String name;
    private boolean enabled;
    private int octaves;
    private double persistence;
    private double lacunarity;
    private double scale;
    private double opacity;
    private BlendMode blendMode;
    private NoiseType noiseType;
    private long seedOffset;
}
