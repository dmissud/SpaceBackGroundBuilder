package org.dbs.sbgb.domain.model.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoiseTexture {
    private Integer noiseOctaves;
    private Double noisePersistence;
    private Double noiseLacunarity;
    private Double noiseScale;
}
