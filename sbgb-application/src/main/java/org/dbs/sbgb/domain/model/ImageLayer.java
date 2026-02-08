package org.dbs.sbgb.domain.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class ImageLayer {
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
