package org.dbs.sbgb.infrastructure.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoiseTextureEntity {
    private Integer noiseOctaves;
    private Double noisePersistence;
    private Double noiseLacunarity;
    private Double noiseScale;
}
