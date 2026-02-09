package org.dbs.sbgb.domain.model;

import lombok.Builder;
import lombok.Value;
import org.dbs.sbgb.domain.model.parameters.*;

/**
 * Parameters for galaxy generation
 * Contains both geometric parameters (spiral structure) and noise parameters (texture)
 *
 * MIGRATION IN PROGRESS: This class is being refactored to use Value Objects.
 * New code should use the Value Object accessors (getCoreParameters(), etc.)
 * Legacy fields are kept for backward compatibility during migration.
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

    // Star field parameters (applicable to ALL galaxy types)
    @Builder.Default
    double starDensity = 0.0;
    @Builder.Default
    int maxStarSize = 4;
    @Builder.Default
    boolean diffractionSpikes = false;
    @Builder.Default
    int spikeCount = 4;

    // Multi-layer noise parameters (applicable to ALL galaxy types)
    @Builder.Default
    boolean multiLayerNoiseEnabled = false;
    @Builder.Default
    double macroLayerScale = 0.3;
    @Builder.Default
    double macroLayerWeight = 0.5;
    @Builder.Default
    double mesoLayerScale = 1.0;
    @Builder.Default
    double mesoLayerWeight = 0.35;
    @Builder.Default
    double microLayerScale = 3.0;
    @Builder.Default
    double microLayerWeight = 0.15;

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

    // === COMPATIBILITY METHODS (migration helpers) ===

    /**
     * Get core size with fallback to legacy field
     */
    public double getCoreSize() {
        return coreParameters != null ? coreParameters.getCoreSize() : coreSize;
    }

    /**
     * Get galaxy radius with fallback to legacy field
     */
    public double getGalaxyRadius() {
        return coreParameters != null ? coreParameters.getGalaxyRadius() : galaxyRadius;
    }

    /**
     * Get noise octaves with fallback to legacy field
     */
    public int getNoiseOctaves() {
        return noiseTextureParameters != null ? noiseTextureParameters.getOctaves() : noiseOctaves;
    }

    /**
     * Get noise persistence with fallback to legacy field
     */
    public double getNoisePersistence() {
        return noiseTextureParameters != null ? noiseTextureParameters.getPersistence() : noisePersistence;
    }

    /**
     * Get noise lacunarity with fallback to legacy field
     */
    public double getNoiseLacunarity() {
        return noiseTextureParameters != null ? noiseTextureParameters.getLacunarity() : noiseLacunarity;
    }

    /**
     * Get noise scale with fallback to legacy field
     */
    public double getNoiseScale() {
        return noiseTextureParameters != null ? noiseTextureParameters.getScale() : noiseScale;
    }

    /**
     * Get warp strength with fallback to legacy field
     */
    public double getWarpStrength() {
        return domainWarpParameters != null ? domainWarpParameters.getWarpStrength() : warpStrength;
    }

    /**
     * Get star density with fallback to legacy field
     */
    public double getStarDensity() {
        return starFieldParameters != null ? starFieldParameters.getStarDensity() : starDensity;
    }

    /**
     * Get max star size with fallback to legacy field
     */
    public int getMaxStarSize() {
        return starFieldParameters != null ? starFieldParameters.getMaxStarSize() : maxStarSize;
    }

    /**
     * Check if diffraction spikes enabled with fallback to legacy field
     */
    public boolean isDiffractionSpikes() {
        return starFieldParameters != null ? starFieldParameters.isDiffractionSpikes() : diffractionSpikes;
    }

    /**
     * Get spike count with fallback to legacy field
     */
    public int getSpikeCount() {
        return starFieldParameters != null ? starFieldParameters.getSpikeCount() : spikeCount;
    }

    /**
     * Check if multi-layer noise enabled with fallback to legacy field
     */
    public boolean isMultiLayerNoiseEnabled() {
        return multiLayerNoiseParameters != null ? multiLayerNoiseParameters.isEnabled() : multiLayerNoiseEnabled;
    }

    /**
     * Get macro layer scale with fallback to legacy field
     */
    public double getMacroLayerScale() {
        return multiLayerNoiseParameters != null ? multiLayerNoiseParameters.getMacroLayerScale() : macroLayerScale;
    }

    /**
     * Get macro layer weight with fallback to legacy field
     */
    public double getMacroLayerWeight() {
        return multiLayerNoiseParameters != null ? multiLayerNoiseParameters.getMacroLayerWeight() : macroLayerWeight;
    }

    /**
     * Get meso layer scale with fallback to legacy field
     */
    public double getMesoLayerScale() {
        return multiLayerNoiseParameters != null ? multiLayerNoiseParameters.getMesoLayerScale() : mesoLayerScale;
    }

    /**
     * Get meso layer weight with fallback to legacy field
     */
    public double getMesoLayerWeight() {
        return multiLayerNoiseParameters != null ? multiLayerNoiseParameters.getMesoLayerWeight() : mesoLayerWeight;
    }

    /**
     * Get micro layer scale with fallback to legacy field
     */
    public double getMicroLayerScale() {
        return multiLayerNoiseParameters != null ? multiLayerNoiseParameters.getMicroLayerScale() : microLayerScale;
    }

    /**
     * Get micro layer weight with fallback to legacy field
     */
    public double getMicroLayerWeight() {
        return multiLayerNoiseParameters != null ? multiLayerNoiseParameters.getMicroLayerWeight() : microLayerWeight;
    }

    /**
     * Get number of arms (spiral-specific) with fallback to legacy field
     */
    public int getNumberOfArms() {
        return spiralParameters != null ? spiralParameters.getNumberOfArms() : numberOfArms;
    }

    /**
     * Get arm width (spiral-specific) with fallback to legacy field
     */
    public double getArmWidth() {
        return spiralParameters != null ? spiralParameters.getArmWidth() : armWidth;
    }

    /**
     * Get arm rotation (spiral-specific) with fallback to legacy field
     */
    public double getArmRotation() {
        return spiralParameters != null ? spiralParameters.getArmRotation() : armRotation;
    }
}
