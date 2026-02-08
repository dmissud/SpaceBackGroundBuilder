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

    @Builder.Default
    GalaxyType galaxyType = GalaxyType.SPIRAL;

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

    // Voronoi cluster parameters (nullable, only used when galaxyType == VORONOI_CLUSTER)
    Integer clusterCount;
    Double clusterSize;
    Double clusterConcentration;

    // Elliptical parameters (nullable, only used when galaxyType == ELLIPTICAL)
    Double sersicIndex;
    Double axisRatio;
    Double orientationAngle;

    // Ring parameters (nullable, only used when galaxyType == RING)
    Double ringRadius;
    Double ringWidth;
    Double ringIntensity;
    Double coreToRingRatio;

    // Irregular parameters (nullable, only used when galaxyType == IRREGULAR)
    Double irregularity;
    Integer irregularClumpCount;
    Double irregularClumpSize;

    // Domain warping parameter (applicable to ALL galaxy types)
    @Builder.Default
    double warpStrength = 0.0;

    /**
     * Create default parameters for a classic spiral galaxy
     */
    public static GalaxyParameters createDefault() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.SPIRAL)
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
            .galaxyType(GalaxyType.SPIRAL)
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
            .galaxyType(GalaxyType.SPIRAL)
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

    /**
     * Create default parameters for a Voronoi cluster galaxy
     */
    public static GalaxyParameters createDefaultVoronoi() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.VORONOI_CLUSTER)
            .coreSize(0.05)
            .galaxyRadius(1500.0)
            .noiseOctaves(4)
            .noisePersistence(0.5)
            .noiseLacunarity(2.0)
            .noiseScale(200.0)
            .clusterCount(80)
            .clusterSize(60.0)
            .clusterConcentration(0.7)
            .build();
    }

    /**
     * Create parameters for a dense Voronoi cluster galaxy
     */
    public static GalaxyParameters createDenseVoronoi() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.VORONOI_CLUSTER)
            .coreSize(0.08)
            .galaxyRadius(1500.0)
            .noiseOctaves(5)
            .noisePersistence(0.6)
            .noiseLacunarity(2.2)
            .noiseScale(150.0)
            .clusterCount(200)
            .clusterSize(40.0)
            .clusterConcentration(0.85)
            .build();
    }

    /**
     * Create parameters for a sparse Voronoi cluster galaxy
     */
    public static GalaxyParameters createSparseVoronoi() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.VORONOI_CLUSTER)
            .coreSize(0.03)
            .galaxyRadius(1500.0)
            .noiseOctaves(3)
            .noisePersistence(0.4)
            .noiseLacunarity(1.8)
            .noiseScale(250.0)
            .clusterCount(30)
            .clusterSize(90.0)
            .clusterConcentration(0.4)
            .build();
    }

    public static GalaxyParameters createDefaultElliptical() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.ELLIPTICAL)
            .coreSize(0.05)
            .galaxyRadius(1500.0)
            .noiseOctaves(4)
            .noisePersistence(0.5)
            .noiseLacunarity(2.0)
            .noiseScale(200.0)
            .sersicIndex(4.0)
            .axisRatio(0.7)
            .orientationAngle(45.0)
            .build();
    }

    public static GalaxyParameters createRoundElliptical() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.ELLIPTICAL)
            .coreSize(0.08)
            .galaxyRadius(1500.0)
            .noiseOctaves(4)
            .noisePersistence(0.5)
            .noiseLacunarity(2.0)
            .noiseScale(200.0)
            .sersicIndex(2.0)
            .axisRatio(0.95)
            .orientationAngle(0.0)
            .build();
    }

    public static GalaxyParameters createFlatElliptical() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.ELLIPTICAL)
            .coreSize(0.04)
            .galaxyRadius(1500.0)
            .noiseOctaves(4)
            .noisePersistence(0.5)
            .noiseLacunarity(2.0)
            .noiseScale(200.0)
            .sersicIndex(6.0)
            .axisRatio(0.4)
            .orientationAngle(30.0)
            .build();
    }

    /**
     * Create default parameters for a ring galaxy (Hoag's Object style)
     */
    public static GalaxyParameters createDefaultRing() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.RING)
            .coreSize(0.05)
            .galaxyRadius(1500.0)
            .noiseOctaves(4)
            .noisePersistence(0.5)
            .noiseLacunarity(2.0)
            .noiseScale(200.0)
            .ringRadius(900.0)
            .ringWidth(150.0)
            .ringIntensity(1.0)
            .coreToRingRatio(0.3)
            .build();
    }

    /**
     * Create parameters for a wide ring galaxy
     */
    public static GalaxyParameters createWideRing() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.RING)
            .coreSize(0.03)
            .galaxyRadius(1500.0)
            .noiseOctaves(4)
            .noisePersistence(0.5)
            .noiseLacunarity(2.0)
            .noiseScale(200.0)
            .ringRadius(1000.0)
            .ringWidth(250.0)
            .ringIntensity(0.8)
            .coreToRingRatio(0.2)
            .build();
    }

    /**
     * Create parameters for a bright ring galaxy with prominent core
     */
    public static GalaxyParameters createBrightRing() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.RING)
            .coreSize(0.08)
            .galaxyRadius(1500.0)
            .noiseOctaves(5)
            .noisePersistence(0.6)
            .noiseLacunarity(2.2)
            .noiseScale(180.0)
            .ringRadius(800.0)
            .ringWidth(120.0)
            .ringIntensity(1.2)
            .coreToRingRatio(0.5)
            .build();
    }

    /**
     * Create default parameters for an irregular galaxy (Small Magellanic Cloud style)
     */
    public static GalaxyParameters createDefaultIrregular() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.IRREGULAR)
            .coreSize(0.03)
            .galaxyRadius(1500.0)
            .noiseOctaves(6)
            .noisePersistence(0.7)
            .noiseLacunarity(2.5)
            .noiseScale(150.0)
            .irregularity(0.8)
            .irregularClumpCount(15)
            .irregularClumpSize(80.0)
            .build();
    }

    /**
     * Create parameters for a chaotic irregular galaxy
     */
    public static GalaxyParameters createChaoticIrregular() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.IRREGULAR)
            .coreSize(0.02)
            .galaxyRadius(1500.0)
            .noiseOctaves(8)
            .noisePersistence(0.8)
            .noiseLacunarity(3.0)
            .noiseScale(120.0)
            .irregularity(0.95)
            .irregularClumpCount(25)
            .irregularClumpSize(60.0)
            .build();
    }

    /**
     * Create parameters for a dwarf irregular galaxy
     */
    public static GalaxyParameters createDwarfIrregular() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.IRREGULAR)
            .coreSize(0.05)
            .galaxyRadius(1500.0)
            .noiseOctaves(5)
            .noisePersistence(0.6)
            .noiseLacunarity(2.0)
            .noiseScale(200.0)
            .irregularity(0.7)
            .irregularClumpCount(8)
            .irregularClumpSize(100.0)
            .build();
    }
}
