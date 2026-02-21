package org.dbs.sbgb.infrastructure.persistence.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dbs.sbgb.domain.model.NoiseType;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageStructureEntity {
    private int height;
    private int width;
    private int seed;
    private int octaves;
    private double persistence;
    private double lacunarity;
    private double scale;
    private String preset;
    private boolean useMultiLayer;
    @Enumerated(EnumType.STRING)
    private NoiseType noiseType;
}
