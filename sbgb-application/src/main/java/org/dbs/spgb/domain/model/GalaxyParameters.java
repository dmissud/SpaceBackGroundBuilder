package org.dbs.spgb.domain.model;

import lombok.Builder;
import lombok.Value;

/**
 * Parameters for galaxy generation
 * Contains both geometric parameters (spiral structure) and noise parameters (texture)
 */
@Value
@Builder
public class GalaxyParameters {

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

    /**
     * Create default parameters for a classic spiral galaxy
     */
    public static GalaxyParameters createDefault() {
        return GalaxyParameters.builder()
            .numberOfArms(2)
            .armWidth(80.0)
            .armRotation(4.0)
            .coreSize(0.05)
            .galaxyRadius(1500.0)
            .noiseOctaves(4)
            .noisePersistence(0.5)
            .noiseLacunarity(2.0)
            .noiseScale(200.0)
            .build();
    }

    /**
     * Create parameters for a barred spiral galaxy
     */
    public static GalaxyParameters createBarredSpiral() {
        return GalaxyParameters.builder()
            .numberOfArms(2)
            .armWidth(100.0)
            .armRotation(3.0)
            .coreSize(0.08)
            .galaxyRadius(1500.0)
            .noiseOctaves(5)
            .noisePersistence(0.6)
            .noiseLacunarity(2.2)
            .noiseScale(150.0)
            .build();
    }

    /**
     * Create parameters for a multi-arm galaxy (like M51)
     */
    public static GalaxyParameters createMultiArm() {
        return GalaxyParameters.builder()
            .numberOfArms(3)
            .armWidth(70.0)
            .armRotation(5.0)
            .coreSize(0.04)
            .galaxyRadius(1500.0)
            .noiseOctaves(6)
            .noisePersistence(0.55)
            .noiseLacunarity(2.1)
            .noiseScale(180.0)
            .build();
    }
}
