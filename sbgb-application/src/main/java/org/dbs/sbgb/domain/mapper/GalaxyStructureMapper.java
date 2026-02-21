package org.dbs.sbgb.domain.mapper;

import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.constant.GalaxyDefaults;
import org.dbs.sbgb.domain.model.*;
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
                        .colorConfig(buildColorConfigVO(cmd.getColorParameters()))
                                .build();
        }

        public GalaxyParameters toGalaxyParameters(GalaxyRequestCmd cmd) {
                GalaxyType galaxyType = parseGalaxyType(cmd.getGalaxyType());

                return GalaxyParameters.builder()
                                .galaxyType(galaxyType)
                        .coreParameters(buildCoreParameters(cmd))
                        .noiseTextureParameters(buildNoiseTextureParameters(cmd.getNoiseParameters()))
                        .domainWarpParameters(buildDomainWarpParameters(cmd))
                        .starFieldParameters(buildStarFieldParameters(cmd.getStarFieldParameters()))
                        .multiLayerNoiseParameters(buildMultiLayerNoiseParameters(cmd.getMultiLayerNoiseParameters()))
                        .spiralParameters(buildSpiralParameters(cmd.getSpiralParameters()))
                        .voronoiParameters(buildVoronoiParameters(cmd.getVoronoiParameters()))
                        .ellipticalParameters(buildEllipticalParameters(cmd.getEllipticalParameters()))
                        .ringParameters(buildRingParameters(cmd.getRingParameters()))
                        .irregularParameters(buildIrregularParameters(cmd.getIrregularParameters()))
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

        private org.dbs.sbgb.domain.model.vo.IrregularStructure buildIrregularStructureVO(IrregularParameters irregular) {
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
                        .galaxyRadius(defaultIfNull(cmd.getGalaxyRadius(), GalaxyDefaults.DEFAULT_GALAXY_RADIUS))
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
                        return null;
                }
                return SpiralStructureParameters.builder()
                        .numberOfArms(defaultIfNull(spiral.numberOfArms(), GalaxyDefaults.DEFAULT_SPIRAL_ARMS))
                        .armWidth(defaultIfNull(spiral.armWidth(), GalaxyDefaults.DEFAULT_ARM_WIDTH))
                        .armRotation(defaultIfNull(spiral.armRotation(), GalaxyDefaults.DEFAULT_ARM_ROTATION))
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
}
