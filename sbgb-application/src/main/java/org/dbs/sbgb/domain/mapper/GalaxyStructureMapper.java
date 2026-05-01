package org.dbs.sbgb.domain.mapper;

import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.constant.GalaxyDefaults;
import org.dbs.sbgb.domain.model.*;
import org.dbs.sbgb.domain.model.densitywave.DensityWaveGalaxyParams;
import org.dbs.sbgb.domain.model.parameters.*;
import org.dbs.sbgb.port.in.*;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
@Slf4j
public class GalaxyStructureMapper {

        public GalaxyStructure toGalaxyStructure(GalaxyRequestCmd cmd) {
                return GalaxyStructure.builder()
                                .width(cmd.getWidth())
                                .height(cmd.getHeight())
                                .seed(cmd.getSeed())
                                .galaxyType(cmd.getGalaxyType())
                                .warpStrength(cmd.getWarpStrength())
                                .spiralStructure(buildSpiralStructureVO(cmd))
                                .noiseTexture(buildNoiseTextureVO(cmd.getNoiseParameters()))
                                .voronoiCluster(buildVoronoiClusterVO(cmd.getVoronoiParameters()))
                                .ellipticalStructure(buildEllipticalStructureVO(cmd.getEllipticalParameters()))
                                .ringStructure(buildRingStructureVO(cmd.getRingParameters()))
                                .irregularStructure(buildIrregularStructureVO(cmd.getIrregularParameters()))
                                .starField(buildStarFieldVO(cmd.getStarFieldParameters()))
                                .multiLayerNoise(buildMultiLayerNoiseVO(cmd.getMultiLayerNoiseParameters()))
                                .bloomConfig(buildBloomConfigVO(cmd.getBloomParameters()))
                                .colorConfig(buildColorConfigVO(cmd.getColorParameters()))
                                .build();
        }

        public GalaxyParameters toGalaxyParameters(GalaxyRequestCmd cmd) {
                // If preset is specified, use preset parameters
                if (cmd.getPreset() != null && !cmd.getPreset().isBlank()) {
                        return createParametersFromPreset(cmd.getPreset());
                }

                if ("DENSITY_WAVE".equalsIgnoreCase(cmd.getGalaxyType())) {
                        return buildDensityWaveParameters(cmd);
                }

                // Otherwise, build from individual parameters
                GalaxyType galaxyType = parseGalaxyType(cmd.getGalaxyType());

                return GalaxyParameters.builder()
                                .galaxyType(galaxyType)
                                .coreParameters(buildCoreParameters(cmd))
                                .noiseTextureParameters(buildNoiseTextureParameters(cmd.getNoiseParameters()))
                                .domainWarpParameters(buildDomainWarpParameters(cmd))
                                .starFieldParameters(buildStarFieldParameters(cmd.getStarFieldParameters()))
                                .multiLayerNoiseParameters(
                                                buildMultiLayerNoiseParameters(cmd.getMultiLayerNoiseParameters()))
                                .spiralParameters(buildSpiralParameters(cmd.getSpiralParameters()))
                                .voronoiParameters(buildVoronoiParameters(cmd.getVoronoiParameters()))
                                .ellipticalParameters(buildEllipticalParameters(cmd.getEllipticalParameters()))
                                .ringParameters(buildRingParameters(cmd.getRingParameters()))
                                .irregularParameters(buildIrregularParameters(cmd.getIrregularParameters()))
                                .bloomParameters(buildBloomParameters(cmd.getBloomParameters()))
                                .build();
        }

        public GalaxyColorCalculator createColorCalculator(ColorParameters colorParams) {
                // Use gradient palette if specified, otherwise use custom colors
                String palette = colorParams.colorPalette();
                if (palette != null && !palette.isBlank() && !palette.equals("CUSTOM")) {
                        try {
                                ColorPalette colorPalette = ColorPalette.valueOf(palette);
                                return colorPalette.createCalculator();
                        } catch (IllegalArgumentException e) {
                                log.warn("Invalid color palette name provided: {}. Falling back to custom colors.",
                                                palette);
                        }
                }

                // Fall back to custom colors from hex strings
                Color spaceBackground = parseColor(colorParams.spaceBackgroundColor());
                Color core = parseColor(colorParams.coreColor());
                Color arms = parseColor(colorParams.armColor());
                Color outer = parseColor(colorParams.outerColor());

                return new DefaultGalaxyColorCalculator(spaceBackground, core, arms, outer);
        }

        private <T> T defaultIfNull(T value, T defaultValue) {
                return value != null ? value : defaultValue;
        }

        private GalaxyType parseGalaxyType(String galaxyTypeStr) {
                if (galaxyTypeStr == null || galaxyTypeStr.isBlank()) {
                        return GalaxyDefaults.DEFAULT_GALAXY_TYPE;
                }
                return GalaxyType.valueOf(galaxyTypeStr);
        }

        private Color parseColor(String hex) {
                return Color.decode(hex);
        }

        private org.dbs.sbgb.domain.model.vo.SpiralStructure buildSpiralStructureVO(GalaxyRequestCmd cmd) {
                SpiralParameters spiral = cmd.getSpiralParameters();
                return org.dbs.sbgb.domain.model.vo.SpiralStructure.builder()
                                .numberOfArms(spiral != null
                                                ? defaultIfNull(spiral.numberOfArms(),
                                                                GalaxyDefaults.DEFAULT_SPIRAL_ARMS)
                                                : GalaxyDefaults.DEFAULT_SPIRAL_ARMS)
                                .armWidth(spiral != null
                                                ? defaultIfNull(spiral.armWidth(),
                                                                GalaxyDefaults.DEFAULT_ARM_WIDTH)
                                                : GalaxyDefaults.DEFAULT_ARM_WIDTH)
                                .armRotation(spiral != null
                                                ? defaultIfNull(spiral.armRotation(),
                                                                GalaxyDefaults.DEFAULT_ARM_ROTATION)
                                                : GalaxyDefaults.DEFAULT_ARM_ROTATION)
                                .darkLaneOpacity(spiral != null
                                                ? defaultIfNull(spiral.darkLaneOpacity(),
                                                                0.0)
                                                : 0.0)
                                .coreSize(defaultIfNull(cmd.getCoreSize(),
                                                GalaxyDefaults.DEFAULT_CORE_SIZE))
                                .galaxyRadius(defaultIfNull(cmd.getGalaxyRadius(),
                                                GalaxyDefaults.DEFAULT_GALAXY_RADIUS))
                                .build();
        }

        private org.dbs.sbgb.domain.model.vo.NoiseTexture buildNoiseTextureVO(NoiseParameters noise) {
                return org.dbs.sbgb.domain.model.vo.NoiseTexture.builder()
                                .noiseOctaves(noise.octaves())
                                .noisePersistence(noise.persistence())
                                .noiseLacunarity(noise.lacunarity())
                                .noiseScale(noise.scale())
                                .build();
        }

        private org.dbs.sbgb.domain.model.vo.VoronoiCluster buildVoronoiClusterVO(VoronoiParameters voronoi) {
                return org.dbs.sbgb.domain.model.vo.VoronoiCluster.builder()
                                .clusterCount(voronoi != null ? voronoi.clusterCount() : null)
                                .clusterSize(voronoi != null ? voronoi.clusterSize() : null)
                                .clusterConcentration(voronoi != null ? voronoi.clusterConcentration() : null)
                                .build();
        }

        private org.dbs.sbgb.domain.model.vo.EllipticalStructure buildEllipticalStructureVO(
                        EllipticalParameters elliptical) {
                return org.dbs.sbgb.domain.model.vo.EllipticalStructure.builder()
                                .sersicIndex(elliptical != null ? elliptical.sersicIndex() : null)
                                .axisRatio(elliptical != null ? elliptical.axisRatio() : null)
                                .orientationAngle(elliptical != null ? elliptical.orientationAngle() : null)
                                .build();
        }

        private org.dbs.sbgb.domain.model.vo.RingStructure buildRingStructureVO(RingParameters ring) {
                return org.dbs.sbgb.domain.model.vo.RingStructure.builder()
                                .ringRadius(ring != null ? ring.ringRadius() : null)
                                .ringWidth(ring != null ? ring.ringWidth() : null)
                                .ringIntensity(ring != null ? ring.ringIntensity() : null)
                                .coreToRingRatio(ring != null ? ring.coreToRingRatio() : null)
                                .build();
        }

        private org.dbs.sbgb.domain.model.vo.IrregularStructure buildIrregularStructureVO(
                        IrregularParameters irregular) {
                return org.dbs.sbgb.domain.model.vo.IrregularStructure.builder()
                                .irregularity(irregular != null ? irregular.irregularity() : null)
                                .irregularClumpCount(irregular != null ? irregular.irregularClumpCount() : null)
                                .irregularClumpSize(irregular != null ? irregular.irregularClumpSize() : null)
                                .build();
        }

        private org.dbs.sbgb.domain.model.vo.StarField buildStarFieldVO(
                        org.dbs.sbgb.port.in.StarFieldParameters starField) {
                return org.dbs.sbgb.domain.model.vo.StarField.builder()
                                .starFieldEnabled(starField.enabled())
                                .starDensity(starField.density())
                                .maxStarSize(starField.maxStarSize())
                                .diffractionSpikes(starField.diffractionSpikes())
                                .spikeCount(starField.spikeCount())
                                .build();
        }

        private org.dbs.sbgb.domain.model.vo.MultiLayerNoise buildMultiLayerNoiseVO(
                        org.dbs.sbgb.port.in.MultiLayerNoiseParameters multiLayer) {
                return org.dbs.sbgb.domain.model.vo.MultiLayerNoise.builder()
                                .multiLayerNoiseEnabled(multiLayer.enabled())
                                .macroLayerScale(multiLayer.macroLayerScale())
                                .macroLayerWeight(multiLayer.macroLayerWeight())
                                .mesoLayerScale(multiLayer.mesoLayerScale())
                                .mesoLayerWeight(multiLayer.mesoLayerWeight())
                                .microLayerScale(multiLayer.microLayerScale())
                                .microLayerWeight(multiLayer.microLayerWeight())
                                .build();
        }

        private org.dbs.sbgb.domain.model.vo.BloomConfig buildBloomConfigVO(
                        org.dbs.sbgb.port.in.BloomParameters bloom) {
                return org.dbs.sbgb.domain.model.vo.BloomConfig.builder()
                                .bloomEnabled(bloom.enabled())
                                .bloomRadius(bloom.bloomRadius())
                                .bloomIntensity(bloom.bloomIntensity())
                                .bloomThreshold(bloom.bloomThreshold())
                                .build();
        }

        private org.dbs.sbgb.domain.model.parameters.BloomParameters buildBloomParameters(
                        org.dbs.sbgb.port.in.BloomParameters bloom) {
                return org.dbs.sbgb.domain.model.parameters.BloomParameters.builder()
                                .enabled(bloom.enabled())
                                .bloomRadius(bloom.bloomRadius())
                                .bloomIntensity(bloom.bloomIntensity())
                                .bloomThreshold(bloom.bloomThreshold())
                                .build();
        }

        private org.dbs.sbgb.domain.model.vo.ColorConfig buildColorConfigVO(ColorParameters color) {
                return org.dbs.sbgb.domain.model.vo.ColorConfig.builder()
                                .colorPalette(color.colorPalette())
                                .spaceBackgroundColor(color.spaceBackgroundColor())
                                .coreColor(color.coreColor())
                                .armColor(color.armColor())
                                .outerColor(color.outerColor())
                                .build();
        }

        private CoreParameters buildCoreParameters(GalaxyRequestCmd cmd) {
                return CoreParameters.builder()
                                .coreSize(defaultIfNull(cmd.getCoreSize(), GalaxyDefaults.DEFAULT_CORE_SIZE))
                                .galaxyRadius(defaultIfNull(cmd.getGalaxyRadius(),
                                                GalaxyDefaults.DEFAULT_GALAXY_RADIUS))
                                .build();
        }

        private NoiseTextureParameters buildNoiseTextureParameters(NoiseParameters noise) {
                return NoiseTextureParameters.builder()
                                .octaves(noise.octaves())
                                .persistence(noise.persistence())
                                .lacunarity(noise.lacunarity())
                                .scale(noise.scale())
                                .build();
        }

        private DomainWarpParameters buildDomainWarpParameters(GalaxyRequestCmd cmd) {
                return DomainWarpParameters.builder()
                                .warpStrength(cmd.getWarpStrength())
                                .build();
        }

        private org.dbs.sbgb.domain.model.parameters.StarFieldParameters buildStarFieldParameters(
                        org.dbs.sbgb.port.in.StarFieldParameters starField) {
                return org.dbs.sbgb.domain.model.parameters.StarFieldParameters.builder()
                                .enabled(starField.enabled())
                                .starDensity(starField.density())
                                .maxStarSize(starField.maxStarSize())
                                .diffractionSpikes(starField.diffractionSpikes())
                                .spikeCount(starField.spikeCount())
                                .build();
        }

        private org.dbs.sbgb.domain.model.parameters.MultiLayerNoiseParameters buildMultiLayerNoiseParameters(
                        org.dbs.sbgb.port.in.MultiLayerNoiseParameters multiLayer) {
                return org.dbs.sbgb.domain.model.parameters.MultiLayerNoiseParameters.builder()
                                .enabled(multiLayer.enabled())
                                .macroLayerScale(multiLayer.macroLayerScale())
                                .macroLayerWeight(multiLayer.macroLayerWeight())
                                .mesoLayerScale(multiLayer.mesoLayerScale())
                                .mesoLayerWeight(multiLayer.mesoLayerWeight())
                                .microLayerScale(multiLayer.microLayerScale())
                                .microLayerWeight(multiLayer.microLayerWeight())
                                .build();
        }

        private SpiralStructureParameters buildSpiralParameters(SpiralParameters spiral) {
                if (spiral == null) {
                        return SpiralStructureParameters.builder()
                                        .numberOfArms(GalaxyDefaults.DEFAULT_SPIRAL_ARMS)
                                        .armWidth(GalaxyDefaults.DEFAULT_ARM_WIDTH)
                                        .armRotation(GalaxyDefaults.DEFAULT_ARM_ROTATION)
                                        .darkLaneOpacity(0.0)
                                        .build();
                }
                return SpiralStructureParameters.builder()
                                .numberOfArms(defaultIfNull(spiral.numberOfArms(), GalaxyDefaults.DEFAULT_SPIRAL_ARMS))
                                .armWidth(defaultIfNull(spiral.armWidth(), GalaxyDefaults.DEFAULT_ARM_WIDTH))
                                .armRotation(defaultIfNull(spiral.armRotation(), GalaxyDefaults.DEFAULT_ARM_ROTATION))
                                .darkLaneOpacity(defaultIfNull(spiral.darkLaneOpacity(), 0.0))
                                .build();
        }

        private VoronoiClusterParameters buildVoronoiParameters(VoronoiParameters voronoi) {
                if (voronoi == null) {
                        return null;
                }
                return VoronoiClusterParameters.builder()
                                .clusterCount(voronoi.clusterCount())
                                .clusterSize(voronoi.clusterSize())
                                .clusterConcentration(voronoi.clusterConcentration())
                                .build();
        }

        private EllipticalShapeParameters buildEllipticalParameters(EllipticalParameters elliptical) {
                if (elliptical == null) {
                        return null;
                }
                return EllipticalShapeParameters.builder()
                                .sersicIndex(elliptical.sersicIndex())
                                .axisRatio(elliptical.axisRatio())
                                .orientationAngle(elliptical.orientationAngle())
                                .build();
        }

        private RingStructureParameters buildRingParameters(RingParameters ring) {
                if (ring == null) {
                        return null;
                }
                return RingStructureParameters.builder()
                                .ringRadius(ring.ringRadius())
                                .ringWidth(ring.ringWidth())
                                .ringIntensity(ring.ringIntensity())
                                .coreToRingRatio(ring.coreToRingRatio())
                                .build();
        }

        private IrregularStructureParameters buildIrregularParameters(IrregularParameters irregular) {
                if (irregular == null) {
                        return null;
                }
                return IrregularStructureParameters.builder()
                                .irregularity(irregular.irregularity())
                                .clumpCount(irregular.irregularClumpCount())
                                .clumpSize(irregular.irregularClumpSize())
                                .build();
        }

        private GalaxyParameters buildDensityWaveParameters(GalaxyRequestCmd cmd) {
                DensityWaveGalaxyParams densityWaveParams = cmd.getDensityWaveParameters() != null
                        ? toDensityWaveGalaxyParams(cmd.getDensityWaveParameters())
                        : DensityWaveGalaxyParams.defaultParams(15000.0f, 60000);
                return GalaxyParameters.builder()
                        .galaxyType(GalaxyType.DENSITY_WAVE)
                        .densityWaveParams(densityWaveParams)
                        .build();
        }

        private DensityWaveGalaxyParams toDensityWaveGalaxyParams(DensityWaveParameters p) {
                return new DensityWaveGalaxyParams(
                        p.galaxyRadius(), p.coreRadius(), p.angleOffset(),
                        p.eccentricityInner(), p.eccentricityOuter(),
                        p.starCount(), p.hasDarkMatter(), p.pertN(), p.pertAmp(), p.baseTemp()
                );
        }

        private GalaxyParameters createParametersFromPreset(String preset) {
                switch (preset.toUpperCase()) {
                        case "DEFAULT":
                                return GalaxyParameters.createDefault();
                        case "BARRED_SPIRAL":
                                return GalaxyParameters.createBarredSpiral();
                        case "MULTI_ARM":
                                return GalaxyParameters.createMultiArm();
                        case "VIBRANT_SPIRAL":
                                return GalaxyParameters.createVibrantSpiral();
                        case "DEFAULT_VORONOI":
                                return GalaxyParameters.createDefaultVoronoi();
                        case "DENSE_VORONOI":
                                return GalaxyParameters.createDenseVoronoi();
                        case "SPARSE_VORONOI":
                                return GalaxyParameters.createSparseVoronoi();
                        case "DEFAULT_ELLIPTICAL":
                                return GalaxyParameters.createDefaultElliptical();
                        case "ROUND_ELLIPTICAL":
                                return GalaxyParameters.createRoundElliptical();
                        case "FLAT_ELLIPTICAL":
                                return GalaxyParameters.createFlatElliptical();
                        case "GIANT_ELLIPTICAL":
                                return GalaxyParameters.createGiantElliptical();
                        case "LENTICULAR_ELLIPTICAL":
                                return GalaxyParameters.createLenticularElliptical();
                        case "DEFAULT_LENTICULAR":
                                return GalaxyParameters.createDefaultLenticular();
                        case "DEFAULT_RING":
                                return GalaxyParameters.createDefaultRing();
                        case "WIDE_RING":
                                return GalaxyParameters.createWideRing();
                        case "BRIGHT_RING":
                                return GalaxyParameters.createBrightRing();
                        case "DEFAULT_IRREGULAR":
                                return GalaxyParameters.createDefaultIrregular();
                        case "CHAOTIC_IRREGULAR":
                                return GalaxyParameters.createChaoticIrregular();
                        case "DWARF_IRREGULAR":
                                return GalaxyParameters.createDwarfIrregular();
                        case "DENSITY_WAVE":
                                return GalaxyParameters.createDefaultDensityWave();
                        default:
                                log.warn("Unknown preset '{}', falling back to DEFAULT", preset);
                                return GalaxyParameters.createDefault();
                }
        }
}
