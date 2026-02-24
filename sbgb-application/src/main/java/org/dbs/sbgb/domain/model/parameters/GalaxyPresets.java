package org.dbs.sbgb.domain.model.parameters;

import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;

/**
 * Factory class for generating standard pre-configured GalaxyParameters.
 * Contains purely static factory methods for different galaxy presets.
 */
public final class GalaxyPresets {

        private GalaxyPresets() {
                // Prevent instantiation
        }

        /**
         * Create default parameters for a classic spiral galaxy
         */
        public static GalaxyParameters createDefault() {
                return createFromSpiralPreset(SpiralPresets.CLASSIC);
        }

        public static GalaxyParameters createBarredSpiral() {
                return createFromSpiralPreset(SpiralPresets.BARRED);
        }

        public static GalaxyParameters createMultiArm() {
                return createFromSpiralPreset(SpiralPresets.MULTI_ARM);
        }

        private static GalaxyParameters createFromSpiralPreset(SpiralPresets preset) {
                return GalaxyParameters.builder()
                                .galaxyType(GalaxyType.SPIRAL)
                                .coreParameters(CoreParameters.builder()
                                                .coreSize(preset.coreSize())
                                                .galaxyRadius(preset.galaxyRadius())
                                                .build())
                                .noiseTextureParameters(NoiseTextureParameters.builder()
                                                .octaves(4)
                                                .persistence(0.5)
                                                .lacunarity(2.0)
                                                .scale(200.0)
                                                .build())
                                .spiralParameters(SpiralStructureParameters.builder()
                                                .numberOfArms(preset.numberOfArms())
                                                .armWidth(preset.armWidth())
                                                .armRotation(preset.armRotation())
                                                .darkLaneOpacity(preset.darkLaneOpacity())
                                                .build())
                                .build();
        }

        public static GalaxyParameters createDefaultVoronoi() {
                return createFromVoronoiPreset(VoronoiPresets.CLASSIC);
        }

        public static GalaxyParameters createDenseVoronoi() {
                return createFromVoronoiPreset(VoronoiPresets.DENSE);
        }

        public static GalaxyParameters createSparseVoronoi() {
                return createFromVoronoiPreset(VoronoiPresets.SPARSE);
        }

        private static GalaxyParameters createFromVoronoiPreset(VoronoiPresets preset) {
                return GalaxyParameters.builder()
                                .galaxyType(GalaxyType.VORONOI_CLUSTER)
                                .coreParameters(CoreParameters.builder()
                                                .coreSize(preset.coreSize())
                                                .galaxyRadius(preset.galaxyRadius())
                                                .build())
                                .noiseTextureParameters(NoiseTextureParameters.builder()
                                                .octaves(4)
                                                .persistence(0.5)
                                                .lacunarity(2.0)
                                                .scale(200.0)
                                                .build())
                                .voronoiParameters(VoronoiClusterParameters.builder()
                                                .clusterCount(preset.clusterCount())
                                                .clusterSize(preset.clusterSize())
                                                .clusterConcentration(preset.clusterConcentration())
                                                .build())
                                .build();
        }

        public static GalaxyParameters createDefaultElliptical() {
                return createFromEllipticalPreset(EllipticalPresets.CLASSIC);
        }

        public static GalaxyParameters createRoundElliptical() {
                return createFromEllipticalPreset(EllipticalPresets.ROUND);
        }

        public static GalaxyParameters createFlatElliptical() {
                return createFromEllipticalPreset(EllipticalPresets.FLAT);
        }

        private static GalaxyParameters createFromEllipticalPreset(EllipticalPresets preset) {
                return GalaxyParameters.builder()
                                .galaxyType(GalaxyType.ELLIPTICAL)
                                .coreParameters(CoreParameters.builder()
                                                .coreSize(preset.coreSize())
                                                .galaxyRadius(preset.galaxyRadius())
                                                .build())
                                .noiseTextureParameters(NoiseTextureParameters.builder()
                                                .octaves(4)
                                                .persistence(0.5)
                                                .lacunarity(2.0)
                                                .scale(200.0)
                                                .build())
                                .ellipticalParameters(EllipticalShapeParameters.builder()
                                                .sersicIndex(preset.sersicIndex())
                                                .axisRatio(preset.axisRatio())
                                                .orientationAngle(preset.orientationAngle())
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
         * Create default parameters for an irregular galaxy (Small Magellanic Cloud
         * style)
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
