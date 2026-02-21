package org.dbs.sbgb.domain.model.vo;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Embeddable
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
