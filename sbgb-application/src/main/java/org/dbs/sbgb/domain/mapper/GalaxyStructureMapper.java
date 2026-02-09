package org.dbs.sbgb.domain.mapper;

import org.dbs.sbgb.domain.model.*;
import org.dbs.sbgb.port.in.*;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class GalaxyStructureMapper {

    public GalaxyStructure toGalaxyStructure(GalaxyRequestCmd cmd) {
        NoiseParameters noise = cmd.getNoiseParameters();
        SpiralParameters spiral = cmd.getSpiralParameters();
        VoronoiParameters voronoi = cmd.getVoronoiParameters();
        EllipticalParameters elliptical = cmd.getEllipticalParameters();
        RingParameters ring = cmd.getRingParameters();
        IrregularParameters irregular = cmd.getIrregularParameters();
        StarFieldParameters starField = cmd.getStarFieldParameters();
        MultiLayerNoiseParameters multiLayer = cmd.getMultiLayerNoiseParameters();
        ColorParameters color = cmd.getColorParameters();

        return GalaxyStructure.builder()
                .width(cmd.getWidth())
                .height(cmd.getHeight())
                .seed(cmd.getSeed())
                .galaxyType(cmd.getGalaxyType())
                .numberOfArms(spiral != null ? defaultIfNull(spiral.numberOfArms(), 2) : 2)
                .armWidth(spiral != null ? defaultIfNull(spiral.armWidth(), 80.0) : 80.0)
                .armRotation(spiral != null ? defaultIfNull(spiral.armRotation(), 4.0) : 4.0)
                .coreSize(defaultIfNull(cmd.getCoreSize(), 0.05))
                .galaxyRadius(defaultIfNull(cmd.getGalaxyRadius(), 1500.0))
                .noiseOctaves(noise.octaves())
                .noisePersistence(noise.persistence())
                .noiseLacunarity(noise.lacunarity())
                .noiseScale(noise.scale())
                .clusterCount(voronoi != null ? voronoi.clusterCount() : null)
                .clusterSize(voronoi != null ? voronoi.clusterSize() : null)
                .clusterConcentration(voronoi != null ? voronoi.clusterConcentration() : null)
                .sersicIndex(elliptical != null ? elliptical.sersicIndex() : null)
                .axisRatio(elliptical != null ? elliptical.axisRatio() : null)
                .orientationAngle(elliptical != null ? elliptical.orientationAngle() : null)
                .ringRadius(ring != null ? ring.ringRadius() : null)
                .ringWidth(ring != null ? ring.ringWidth() : null)
                .ringIntensity(ring != null ? ring.ringIntensity() : null)
                .coreToRingRatio(ring != null ? ring.coreToRingRatio() : null)
                .irregularity(irregular != null ? irregular.irregularity() : null)
                .irregularClumpCount(irregular != null ? irregular.irregularClumpCount() : null)
                .irregularClumpSize(irregular != null ? irregular.irregularClumpSize() : null)
                .warpStrength(cmd.getWarpStrength())
                .colorPalette(color.colorPalette())
                .starDensity(starField.density())
                .maxStarSize(starField.maxStarSize())
                .diffractionSpikes(starField.diffractionSpikes())
                .spikeCount(starField.spikeCount())
                .multiLayerNoiseEnabled(multiLayer.enabled())
                .macroLayerScale(multiLayer.macroLayerScale())
                .macroLayerWeight(multiLayer.macroLayerWeight())
                .mesoLayerScale(multiLayer.mesoLayerScale())
                .mesoLayerWeight(multiLayer.mesoLayerWeight())
                .microLayerScale(multiLayer.microLayerScale())
                .microLayerWeight(multiLayer.microLayerWeight())
                .spaceBackgroundColor(color.spaceBackgroundColor())
                .coreColor(color.coreColor())
                .armColor(color.armColor())
                .outerColor(color.outerColor())
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
        StarFieldParameters starField = cmd.getStarFieldParameters();
        MultiLayerNoiseParameters multiLayer = cmd.getMultiLayerNoiseParameters();

        return GalaxyParameters.builder()
                .galaxyType(galaxyType)
                .numberOfArms(spiral != null ? defaultIfNull(spiral.numberOfArms(), 2) : 2)
                .armWidth(spiral != null ? defaultIfNull(spiral.armWidth(), 80.0) : 80.0)
                .armRotation(spiral != null ? defaultIfNull(spiral.armRotation(), 4.0) : 4.0)
                .coreSize(defaultIfNull(cmd.getCoreSize(), 0.05))
                .galaxyRadius(defaultIfNull(cmd.getGalaxyRadius(), 1500.0))
                .noiseOctaves(noise.octaves())
                .noisePersistence(noise.persistence())
                .noiseLacunarity(noise.lacunarity())
                .noiseScale(noise.scale())
                .clusterCount(voronoi != null ? voronoi.clusterCount() : null)
                .clusterSize(voronoi != null ? voronoi.clusterSize() : null)
                .clusterConcentration(voronoi != null ? voronoi.clusterConcentration() : null)
                .sersicIndex(elliptical != null ? elliptical.sersicIndex() : null)
                .axisRatio(elliptical != null ? elliptical.axisRatio() : null)
                .orientationAngle(elliptical != null ? elliptical.orientationAngle() : null)
                .ringRadius(ring != null ? ring.ringRadius() : null)
                .ringWidth(ring != null ? ring.ringWidth() : null)
                .ringIntensity(ring != null ? ring.ringIntensity() : null)
                .coreToRingRatio(ring != null ? ring.coreToRingRatio() : null)
                .irregularity(irregular != null ? irregular.irregularity() : null)
                .irregularClumpCount(irregular != null ? irregular.irregularClumpCount() : null)
                .irregularClumpSize(irregular != null ? irregular.irregularClumpSize() : null)
                .warpStrength(cmd.getWarpStrength())
                .starDensity(starField.density())
                .maxStarSize(starField.maxStarSize())
                .diffractionSpikes(starField.diffractionSpikes())
                .spikeCount(starField.spikeCount())
                .multiLayerNoiseEnabled(multiLayer.enabled())
                .macroLayerScale(multiLayer.macroLayerScale())
                .macroLayerWeight(multiLayer.macroLayerWeight())
                .mesoLayerScale(multiLayer.mesoLayerScale())
                .mesoLayerWeight(multiLayer.mesoLayerWeight())
                .microLayerScale(multiLayer.microLayerScale())
                .microLayerWeight(multiLayer.microLayerWeight())
                .build();
    }

    public GalaxyColorCalculator createColorCalculator(ColorParameters colorParams) {
        // Use gradient palette if specified, otherwise use custom colors
        String palette = colorParams.colorPalette();
        if (palette != null && !palette.isBlank()) {
            try {
                ColorPalette colorPalette = ColorPalette.valueOf(palette);
                return colorPalette.createCalculator();
            } catch (IllegalArgumentException e) {
                // Fall back to custom colors if palette name is invalid
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
            return GalaxyType.SPIRAL;
        }
        return GalaxyType.valueOf(galaxyTypeStr);
    }

    private Color parseColor(String hex) {
        return Color.decode(hex);
    }
}
