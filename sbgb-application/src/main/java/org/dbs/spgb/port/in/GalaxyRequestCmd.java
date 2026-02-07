package org.dbs.spgb.port.in;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GalaxyRequestCmd {
    String name;
    String description;
    int width;
    int height;
    long seed;

    // Spiral structure parameters
    int numberOfArms;
    double armWidth;
    double armRotation;
    double coreSize;
    double galaxyRadius;

    // Noise texture parameters
    int noiseOctaves;
    double noisePersistence;
    double noiseLacunarity;
    double noiseScale;
}
