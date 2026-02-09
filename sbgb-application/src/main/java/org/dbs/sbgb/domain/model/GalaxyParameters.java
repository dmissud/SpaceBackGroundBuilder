package org.dbs.sbgb.domain.model;

import lombok.Builder;
import lombok.Value;
import org.dbs.sbgb.domain.model.parameters.*;

/**
 * Parameters for galaxy generation using Value Objects pattern.
 *
 * This class encapsulates all parameters needed to generate a galaxy image,
 * organized into cohesive Value Objects:
 * - CoreParameters: common core/radius parameters
 * - NoiseTextureParameters: noise generation settings
 * - DomainWarpParameters: spatial warping settings
 * - StarFieldParameters: star overlay settings
 * - MultiLayerNoiseParameters: multi-scale noise settings
 * - Type-specific parameters: SpiralStructureParameters, VoronoiClusterParameters, etc.
 */
@Value
@Builder
public class GalaxyParameters {

    @Builder.Default
    GalaxyType galaxyType = GalaxyType.SPIRAL;

    // === NEW: Value Objects (preferred) ===
    CoreParameters coreParameters;
    NoiseTextureParameters noiseTextureParameters;
    DomainWarpParameters domainWarpParameters;
    StarFieldParameters starFieldParameters;
    MultiLayerNoiseParameters multiLayerNoiseParameters;

    // Type-specific parameters (nullable)
    SpiralStructureParameters spiralParameters;
    VoronoiClusterParameters voronoiParameters;
    EllipticalShapeParameters ellipticalParameters;
    RingStructureParameters ringParameters;
    IrregularStructureParameters irregularParameters;

    /**
     * Create default parameters for a classic spiral galaxy
     */
    public static GalaxyParameters createDefault() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.SPIRAL)
            .coreParameters(CoreParameters.builder()
                .coreSize(0.05)
                .galaxyRadius(1500.0)
                .build())
            .noiseTextureParameters(NoiseTextureParameters.builder()
                .octaves(4)
                .persistence(0.5)
                .lacunarity(2.0)
                .scale(200.0)
                .build())
            .spiralParameters(SpiralStructureParameters.builder()
                .numberOfArms(2)
                .armWidth(80.0)
                .armRotation(4.0)
                .build())
            .build();
    }

    /**
     * Create parameters for a barred spiral galaxy
     */
    public static GalaxyParameters createBarredSpiral() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.SPIRAL)
            .coreParameters(CoreParameters.builder()
                .coreSize(0.08)
                .galaxyRadius(1500.0)
                .build())
            .noiseTextureParameters(NoiseTextureParameters.builder()
                .octaves(5)
                .persistence(0.6)
                .lacunarity(2.2)
                .scale(150.0)
                .build())
            .spiralParameters(SpiralStructureParameters.builder()
                .numberOfArms(2)
                .armWidth(100.0)
                .armRotation(3.0)
                .build())
            .build();
    }

    /**
     * Create parameters for a multi-arm galaxy (like M51)
     */
    public static GalaxyParameters createMultiArm() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.SPIRAL)
            .coreParameters(CoreParameters.builder()
                .coreSize(0.04)
                .galaxyRadius(1500.0)
                .build())
            .noiseTextureParameters(NoiseTextureParameters.builder()
                .octaves(6)
                .persistence(0.55)
                .lacunarity(2.1)
                .scale(180.0)
                .build())
            .spiralParameters(SpiralStructureParameters.builder()
                .numberOfArms(3)
                .armWidth(70.0)
                .armRotation(5.0)
                .build())
            .build();
    }

    /**
     * Create default parameters for a Voronoi cluster galaxy
     */
    public static GalaxyParameters createDefaultVoronoi() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.VORONOI_CLUSTER)
            .coreParameters(CoreParameters.builder()
                .coreSize(0.05)
                .galaxyRadius(1500.0)
                .build())
            .noiseTextureParameters(NoiseTextureParameters.builder()
                .octaves(4)
                .persistence(0.5)
                .lacunarity(2.0)
                .scale(200.0)
                .build())
            .voronoiParameters(VoronoiClusterParameters.builder()
                .clusterCount(80)
                .clusterSize(60.0)
                .clusterConcentration(0.7)
                .build())
            .build();
    }

    /**
     * Create parameters for a dense Voronoi cluster galaxy
     */
    public static GalaxyParameters createDenseVoronoi() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.VORONOI_CLUSTER)
            .coreParameters(CoreParameters.builder()
                .coreSize(0.08)
                .galaxyRadius(1500.0)
                .build())
            .noiseTextureParameters(NoiseTextureParameters.builder()
                .octaves(5)
                .persistence(0.6)
                .lacunarity(2.2)
                .scale(150.0)
                .build())
            .voronoiParameters(VoronoiClusterParameters.builder()
                .clusterCount(200)
                .clusterSize(40.0)
                .clusterConcentration(0.85)
                .build())
            .build();
    }

    /**
     * Create parameters for a sparse Voronoi cluster galaxy
     */
    public static GalaxyParameters createSparseVoronoi() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.VORONOI_CLUSTER)
            .coreParameters(CoreParameters.builder()
                .coreSize(0.03)
                .galaxyRadius(1500.0)
                .build())
            .noiseTextureParameters(NoiseTextureParameters.builder()
                .octaves(3)
                .persistence(0.4)
                .lacunarity(1.8)
                .scale(250.0)
                .build())
            .voronoiParameters(VoronoiClusterParameters.builder()
                .clusterCount(30)
                .clusterSize(90.0)
                .clusterConcentration(0.4)
                .build())
            .build();
    }

    public static GalaxyParameters createDefaultElliptical() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.ELLIPTICAL)
            .coreParameters(CoreParameters.builder()
                .coreSize(0.05)
                .galaxyRadius(1500.0)
                .build())
            .noiseTextureParameters(NoiseTextureParameters.builder()
                .octaves(4)
                .persistence(0.5)
                .lacunarity(2.0)
                .scale(200.0)
                .build())
            .ellipticalParameters(EllipticalShapeParameters.builder()
                .sersicIndex(4.0)
                .axisRatio(0.7)
                .orientationAngle(45.0)
                .build())
            .build();
    }

    public static GalaxyParameters createRoundElliptical() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.ELLIPTICAL)
            .coreParameters(CoreParameters.builder()
                .coreSize(0.08)
                .galaxyRadius(1500.0)
                .build())
            .noiseTextureParameters(NoiseTextureParameters.builder()
                .octaves(4)
                .persistence(0.5)
                .lacunarity(2.0)
                .scale(200.0)
                .build())
            .ellipticalParameters(EllipticalShapeParameters.builder()
                .sersicIndex(2.0)
                .axisRatio(0.95)
                .orientationAngle(0.0)
                .build())
            .build();
    }

    public static GalaxyParameters createFlatElliptical() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.ELLIPTICAL)
            .coreParameters(CoreParameters.builder()
                .coreSize(0.04)
                .galaxyRadius(1500.0)
                .build())
            .noiseTextureParameters(NoiseTextureParameters.builder()
                .octaves(4)
                .persistence(0.5)
                .lacunarity(2.0)
                .scale(200.0)
                .build())
            .ellipticalParameters(EllipticalShapeParameters.builder()
                .sersicIndex(6.0)
                .axisRatio(0.4)
                .orientationAngle(30.0)
                .build())
            .build();
    }

    /**
     * Create default parameters for a ring galaxy (Hoag's Object style)
     */
    public static GalaxyParameters createDefaultRing() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.RING)
            .coreParameters(CoreParameters.builder()
                .coreSize(0.05)
                .galaxyRadius(1500.0)
                .build())
            .noiseTextureParameters(NoiseTextureParameters.builder()
                .octaves(4)
                .persistence(0.5)
                .lacunarity(2.0)
                .scale(200.0)
                .build())
            .ringParameters(RingStructureParameters.builder()
                .ringRadius(900.0)
                .ringWidth(150.0)
                .ringIntensity(1.0)
                .coreToRingRatio(0.3)
                .build())
            .build();
    }

    /**
     * Create parameters for a wide ring galaxy
     */
    public static GalaxyParameters createWideRing() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.RING)
            .coreParameters(CoreParameters.builder()
                .coreSize(0.03)
                .galaxyRadius(1500.0)
                .build())
            .noiseTextureParameters(NoiseTextureParameters.builder()
                .octaves(4)
                .persistence(0.5)
                .lacunarity(2.0)
                .scale(200.0)
                .build())
            .ringParameters(RingStructureParameters.builder()
                .ringRadius(1000.0)
                .ringWidth(250.0)
                .ringIntensity(0.8)
                .coreToRingRatio(0.2)
                .build())
            .build();
    }

    /**
     * Create parameters for a bright ring galaxy with prominent core
     */
    public static GalaxyParameters createBrightRing() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.RING)
            .coreParameters(CoreParameters.builder()
                .coreSize(0.08)
                .galaxyRadius(1500.0)
                .build())
            .noiseTextureParameters(NoiseTextureParameters.builder()
                .octaves(5)
                .persistence(0.6)
                .lacunarity(2.2)
                .scale(180.0)
                .build())
            .ringParameters(RingStructureParameters.builder()
                .ringRadius(800.0)
                .ringWidth(120.0)
                .ringIntensity(1.2)
                .coreToRingRatio(0.5)
                .build())
            .build();
    }

    /**
     * Create default parameters for an irregular galaxy (Small Magellanic Cloud style)
     */
    public static GalaxyParameters createDefaultIrregular() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.IRREGULAR)
            .coreParameters(CoreParameters.builder()
                .coreSize(0.03)
                .galaxyRadius(1500.0)
                .build())
            .noiseTextureParameters(NoiseTextureParameters.builder()
                .octaves(6)
                .persistence(0.7)
                .lacunarity(2.5)
                .scale(150.0)
                .build())
            .irregularParameters(IrregularStructureParameters.builder()
                .irregularity(0.8)
                .clumpCount(15)
                .clumpSize(80.0)
                .build())
            .build();
    }

    /**
     * Create parameters for a chaotic irregular galaxy
     */
    public static GalaxyParameters createChaoticIrregular() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.IRREGULAR)
            .coreParameters(CoreParameters.builder()
                .coreSize(0.02)
                .galaxyRadius(1500.0)
                .build())
            .noiseTextureParameters(NoiseTextureParameters.builder()
                .octaves(8)
                .persistence(0.8)
                .lacunarity(3.0)
                .scale(120.0)
                .build())
            .irregularParameters(IrregularStructureParameters.builder()
                .irregularity(0.95)
                .clumpCount(25)
                .clumpSize(60.0)
                .build())
            .build();
    }

    /**
     * Create parameters for a dwarf irregular galaxy
     */
    public static GalaxyParameters createDwarfIrregular() {
        return GalaxyParameters.builder()
            .galaxyType(GalaxyType.IRREGULAR)
            .coreParameters(CoreParameters.builder()
                .coreSize(0.05)
                .galaxyRadius(1500.0)
                .build())
            .noiseTextureParameters(NoiseTextureParameters.builder()
                .octaves(5)
                .persistence(0.6)
                .lacunarity(2.0)
                .scale(200.0)
                .build())
            .irregularParameters(IrregularStructureParameters.builder()
                .irregularity(0.7)
                .clumpCount(8)
                .clumpSize(100.0)
                .build())
            .build();
    }
}
