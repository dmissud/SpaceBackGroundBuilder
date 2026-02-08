package org.dbs.spgb.spgbexposition.resources.mapper;

import org.dbs.spgb.domain.model.GalaxyImage;
import org.dbs.spgb.domain.model.GalaxyStructure;
import org.dbs.spgb.spgbexposition.resources.dto.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapperGalaxyImage {

    GalaxyImageDTO toDTO(GalaxyImage galaxyImage);

    default GalaxyStructureDTO toStructureDTO(GalaxyStructure structure) {
        if (structure == null) {
            return null;
        }

        return GalaxyStructureDTO.builder()
                .width(structure.getWidth())
                .height(structure.getHeight())
                .seed(structure.getSeed())
                .galaxyType(structure.getGalaxyType())
                .coreSize(structure.getCoreSize())
                .galaxyRadius(structure.getGalaxyRadius())
                .warpStrength(structure.getWarpStrength())
                .noiseParameters(NoiseParametersDTO.builder()
                        .octaves(structure.getNoiseOctaves())
                        .persistence(structure.getNoisePersistence())
                        .lacunarity(structure.getNoiseLacunarity())
                        .scale(structure.getNoiseScale())
                        .build())
                .spiralParameters(structure.getNumberOfArms() != 0 ? SpiralParametersDTO.builder()
                        .numberOfArms(structure.getNumberOfArms())
                        .armWidth(structure.getArmWidth())
                        .armRotation(structure.getArmRotation())
                        .build() : null)
                .voronoiParameters(structure.getClusterCount() != null ? VoronoiParametersDTO.builder()
                        .clusterCount(structure.getClusterCount())
                        .clusterSize(structure.getClusterSize())
                        .clusterConcentration(structure.getClusterConcentration())
                        .build() : null)
                .ellipticalParameters(structure.getSersicIndex() != null ? EllipticalParametersDTO.builder()
                        .sersicIndex(structure.getSersicIndex())
                        .axisRatio(structure.getAxisRatio())
                        .orientationAngle(structure.getOrientationAngle())
                        .build() : null)
                .ringParameters(structure.getRingRadius() != null ? RingParametersDTO.builder()
                        .ringRadius(structure.getRingRadius())
                        .ringWidth(structure.getRingWidth())
                        .ringIntensity(structure.getRingIntensity())
                        .coreToRingRatio(structure.getCoreToRingRatio())
                        .build() : null)
                .irregularParameters(structure.getIrregularity() != null ? IrregularParametersDTO.builder()
                        .irregularity(structure.getIrregularity())
                        .irregularClumpCount(structure.getIrregularClumpCount())
                        .irregularClumpSize(structure.getIrregularClumpSize())
                        .build() : null)
                .starFieldParameters(StarFieldParametersDTO.builder()
                        .density(structure.getStarDensity())
                        .maxStarSize(structure.getMaxStarSize())
                        .diffractionSpikes(structure.isDiffractionSpikes())
                        .spikeCount(structure.getSpikeCount())
                        .build())
                .multiLayerNoiseParameters(MultiLayerNoiseParametersDTO.builder()
                        .enabled(structure.isMultiLayerNoiseEnabled())
                        .macroLayerScale(structure.getMacroLayerScale())
                        .macroLayerWeight(structure.getMacroLayerWeight())
                        .mesoLayerScale(structure.getMesoLayerScale())
                        .mesoLayerWeight(structure.getMesoLayerWeight())
                        .microLayerScale(structure.getMicroLayerScale())
                        .microLayerWeight(structure.getMicroLayerWeight())
                        .build())
                .colorParameters(ColorParametersDTO.builder()
                        .colorPalette(structure.getColorPalette())
                        .spaceBackgroundColor(structure.getSpaceBackgroundColor())
                        .coreColor(structure.getCoreColor())
                        .armColor(structure.getArmColor())
                        .outerColor(structure.getOuterColor())
                        .build())
                .build();
    }
}
