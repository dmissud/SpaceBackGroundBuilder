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
                NoiseParameters noise = cmd.getNoiseParameters();
                SpiralParameters spiral = cmd.getSpiralParameters();
                VoronoiParameters voronoi = cmd.getVoronoiParameters();
                EllipticalParameters elliptical = cmd.getEllipticalParameters();
                RingParameters ring = cmd.getRingParameters();
                IrregularParameters irregular = cmd.getIrregularParameters();
                org.dbs.sbgb.port.in.StarFieldParameters starField = cmd.getStarFieldParameters();
                org.dbs.sbgb.port.in.MultiLayerNoiseParameters multiLayer = cmd.getMultiLayerNoiseParameters();
                ColorParameters color = cmd.getColorParameters();

                return GalaxyStructure.builder()
                                .width(cmd.getWidth())
                                .height(cmd.getHeight())
                                .seed(cmd.getSeed())
                                .galaxyType(cmd.getGalaxyType())
                                .warpStrength(cmd.getWarpStrength())
                                .spiralStructure(org.dbs.sbgb.domain.model.vo.SpiralStructure.builder()
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
                                                .build())
                                .noiseTexture(org.dbs.sbgb.domain.model.vo.NoiseTexture.builder()
                                                .noiseOctaves(noise.octaves())
                                                .noisePersistence(noise.persistence())
                                                .noiseLacunarity(noise.lacunarity())
                                                .noiseScale(noise.scale())
                                                .build())
                                .voronoiCluster(org.dbs.sbgb.domain.model.vo.VoronoiCluster.builder()
                                                .clusterCount(voronoi != null ? voronoi.clusterCount() : null)
                                                .clusterSize(voronoi != null ? voronoi.clusterSize() : null)
                                                .clusterConcentration(
                                                                voronoi != null ? voronoi.clusterConcentration() : null)
                                                .build())
                                .ellipticalStructure(org.dbs.sbgb.domain.model.vo.EllipticalStructure.builder()
                                                .sersicIndex(elliptical != null ? elliptical.sersicIndex() : null)
                                                .axisRatio(elliptical != null ? elliptical.axisRatio() : null)
                                                .orientationAngle(elliptical != null ? elliptical.orientationAngle()
                                                                : null)
                                                .build())
                                .ringStructure(org.dbs.sbgb.domain.model.vo.RingStructure.builder()
                                                .ringRadius(ring != null ? ring.ringRadius() : null)
                                                .ringWidth(ring != null ? ring.ringWidth() : null)
                                                .ringIntensity(ring != null ? ring.ringIntensity() : null)
                                                .coreToRingRatio(ring != null ? ring.coreToRingRatio() : null)
                                                .build())
                                .irregularStructure(org.dbs.sbgb.domain.model.vo.IrregularStructure.builder()
                                                .irregularity(irregular != null ? irregular.irregularity() : null)
                                                .irregularClumpCount(irregular != null ? irregular.irregularClumpCount()
                                                                : null)
                                                .irregularClumpSize(irregular != null ? irregular.irregularClumpSize()
                                                                : null)
                                                .build())
                                .starField(org.dbs.sbgb.domain.model.vo.StarField.builder()
                                                .starFieldEnabled(starField.enabled())
                                                .starDensity(starField.density())
                                                .maxStarSize(starField.maxStarSize())
                                                .diffractionSpikes(starField.diffractionSpikes())
                                                .spikeCount(starField.spikeCount())
                                                .build())
                                .multiLayerNoise(org.dbs.sbgb.domain.model.vo.MultiLayerNoise.builder()
                                                .multiLayerNoiseEnabled(multiLayer.enabled())
                                                .macroLayerScale(multiLayer.macroLayerScale())
                                                .macroLayerWeight(multiLayer.macroLayerWeight())
                                                .mesoLayerScale(multiLayer.mesoLayerScale())
                                                .mesoLayerWeight(multiLayer.mesoLayerWeight())
                                                .microLayerScale(multiLayer.microLayerScale())
                                                .microLayerWeight(multiLayer.microLayerWeight())
                                                .build())
                                .colorConfig(org.dbs.sbgb.domain.model.vo.ColorConfig.builder()
                                                .colorPalette(color.colorPalette())
                                                .spaceBackgroundColor(color.spaceBackgroundColor())
                                                .coreColor(color.coreColor())
                                                .armColor(color.armColor())
                                                .outerColor(color.outerColor())
                                                .build())
                                .build();
        }

        public GalaxyParameters toGalaxyParameters(GalaxyRequestCmd cmd) {
                GalaxyType galaxyType = parseGalaxyType(cmd.getGalaxyType());

                NoiseParameters noise = cmd.getNoiseParameters();
                SpiralParameters spiral = cmd.getSpiralParameters();
                VoronoiParameters voronoi = cmd.getVoronoiParameters();
                EllipticalParameters elliptical = cmd.getEllipticalParameters();
                RingParameters ring = cmd.getRingParameters();
                IrregularParameters irregular = cmd.getIrregularParameters();
                org.dbs.sbgb.port.in.StarFieldParameters starField = cmd.getStarFieldParameters();
                org.dbs.sbgb.port.in.MultiLayerNoiseParameters multiLayer = cmd.getMultiLayerNoiseParameters();

                return GalaxyParameters.builder()
                                .galaxyType(galaxyType)
                                .coreParameters(CoreParameters.builder()
                                                .coreSize(defaultIfNull(cmd.getCoreSize(),
                                                                GalaxyDefaults.DEFAULT_CORE_SIZE))
                                                .galaxyRadius(defaultIfNull(cmd.getGalaxyRadius(),
                                                                GalaxyDefaults.DEFAULT_GALAXY_RADIUS))
                                                .build())
                                .noiseTextureParameters(NoiseTextureParameters.builder()
                                                .octaves(noise.octaves())
                                                .persistence(noise.persistence())
                                                .lacunarity(noise.lacunarity())
                                                .scale(noise.scale())
                                                .build())
                                .domainWarpParameters(DomainWarpParameters.builder()
                                                .warpStrength(cmd.getWarpStrength())
                                                .build())
                                .starFieldParameters(org.dbs.sbgb.domain.model.parameters.StarFieldParameters.builder()
                                                .enabled(starField.enabled())
                                                .starDensity(starField.density())
                                                .maxStarSize(starField.maxStarSize())
                                                .diffractionSpikes(starField.diffractionSpikes())
                                                .spikeCount(starField.spikeCount())
                                                .build())
                                .multiLayerNoiseParameters(
                                                org.dbs.sbgb.domain.model.parameters.MultiLayerNoiseParameters.builder()
                                                                .enabled(multiLayer.enabled())
                                                                .macroLayerScale(multiLayer.macroLayerScale())
                                                                .macroLayerWeight(multiLayer.macroLayerWeight())
                                                                .mesoLayerScale(multiLayer.mesoLayerScale())
                                                                .mesoLayerWeight(multiLayer.mesoLayerWeight())
                                                                .microLayerScale(multiLayer.microLayerScale())
                                                                .microLayerWeight(multiLayer.microLayerWeight())
                                                                .build())
                                .spiralParameters(spiral != null ? SpiralStructureParameters.builder()
                                                .numberOfArms(defaultIfNull(spiral.numberOfArms(),
                                                                GalaxyDefaults.DEFAULT_SPIRAL_ARMS))
                                                .armWidth(defaultIfNull(spiral.armWidth(),
                                                                GalaxyDefaults.DEFAULT_ARM_WIDTH))
                                                .armRotation(defaultIfNull(spiral.armRotation(),
                                                                GalaxyDefaults.DEFAULT_ARM_ROTATION))
                                                .build() : null)
                                .voronoiParameters(voronoi != null ? VoronoiClusterParameters.builder()
                                                .clusterCount(voronoi.clusterCount())
                                                .clusterSize(voronoi.clusterSize())
                                                .clusterConcentration(voronoi.clusterConcentration())
                                                .build() : null)
                                .ellipticalParameters(elliptical != null ? EllipticalShapeParameters.builder()
                                                .sersicIndex(elliptical.sersicIndex())
                                                .axisRatio(elliptical.axisRatio())
                                                .orientationAngle(elliptical.orientationAngle())
                                                .build() : null)
                                .ringParameters(ring != null ? RingStructureParameters.builder()
                                                .ringRadius(ring.ringRadius())
                                                .ringWidth(ring.ringWidth())
                                                .ringIntensity(ring.ringIntensity())
                                                .coreToRingRatio(ring.coreToRingRatio())
                                                .build() : null)
                                .irregularParameters(irregular != null ? IrregularStructureParameters.builder()
                                                .irregularity(irregular.irregularity())
                                                .clumpCount(irregular.irregularClumpCount())
                                                .clumpSize(irregular.irregularClumpSize())
                                                .build() : null)
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
}
