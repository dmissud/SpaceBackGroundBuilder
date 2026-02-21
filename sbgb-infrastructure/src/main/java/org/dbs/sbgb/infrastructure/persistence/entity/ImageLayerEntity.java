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
public class ImageLayerEntity {
    private String name;
    private boolean enabled;
    private int octaves;
    private double persistence;
    private double lacunarity;
    private double scale;
    private double opacity;
    private String blendMode;
    private long seedOffset;
}
