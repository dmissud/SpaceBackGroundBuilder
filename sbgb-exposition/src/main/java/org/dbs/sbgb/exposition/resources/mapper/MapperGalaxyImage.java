package org.dbs.sbgb.exposition.resources.mapper;

import org.dbs.sbgb.domain.model.GalaxyImage;
import org.dbs.sbgb.domain.model.GalaxyStructure;
import org.dbs.sbgb.exposition.resources.dto.*;
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
                                .coreSize(structure.getSpiralStructure() != null
                                                ? structure.getSpiralStructure().getCoreSize()
                                                : null)
                                .galaxyRadius(structure.getSpiralStructure() != null
                                                ? structure.getSpiralStructure().getGalaxyRadius()
                                                : null)
                                .warpStrength(structure.getWarpStrength())
                                .noiseParameters(structure.getNoiseTexture() != null ? NoiseParametersDTO.builder()
                                                .octaves(structure.getNoiseTexture().getNoiseOctaves())
                                                .persistence(structure.getNoiseTexture().getNoisePersistence())
                                                .lacunarity(structure.getNoiseTexture().getNoiseLacunarity())
                                                .scale(structure.getNoiseTexture().getNoiseScale())
                                                .build() : null)
                                .spiralParameters(structure.getSpiralStructure() != null
                                                && structure.getSpiralStructure().getNumberOfArms() != null
                                                && structure.getSpiralStructure().getNumberOfArms() != 0
                                                                ? SpiralParametersDTO.builder()
                                                                                .numberOfArms(structure
                                                                                                .getSpiralStructure()
                                                                                                .getNumberOfArms())
                                                                                .armWidth(structure.getSpiralStructure()
                                                                                                .getArmWidth())
                                                                                .armRotation(structure
                                                                                                .getSpiralStructure()
                                                                                                .getArmRotation())
                                                                                .darkLaneOpacity(structure
                                                                                                .getSpiralStructure()
                                                                                                .getDarkLaneOpacity())
                                                                                .build()
                                                                : null)
                                .voronoiParameters(structure.getVoronoiCluster() != null && structure
                                                .getVoronoiCluster().getClusterCount() != null ? VoronoiParametersDTO
                                                                .builder()
                                                                .clusterCount(structure.getVoronoiCluster()
                                                                                .getClusterCount())
                                                                .clusterSize(structure.getVoronoiCluster()
                                                                                .getClusterSize())
                                                                .clusterConcentration(structure.getVoronoiCluster()
                                                                                .getClusterConcentration())
                                                                .build() : null)
                                .ellipticalParameters(structure.getEllipticalStructure() != null
                                                && structure.getEllipticalStructure().getSersicIndex() != null
                                                                ? EllipticalParametersDTO.builder()
                                                                                .sersicIndex(structure
                                                                                                .getEllipticalStructure()
                                                                                                .getSersicIndex())
                                                                                .axisRatio(structure
                                                                                                .getEllipticalStructure()
                                                                                                .getAxisRatio())
                                                                                .orientationAngle(structure
                                                                                                .getEllipticalStructure()
                                                                                                .getOrientationAngle())
                                                                                .build()
                                                                : null)
                                .ringParameters(structure.getRingStructure() != null && structure
                                                .getRingStructure().getRingRadius() != null ? RingParametersDTO
                                                                .builder()
                                                                .ringRadius(structure.getRingStructure()
                                                                                .getRingRadius())
                                                                .ringWidth(structure.getRingStructure().getRingWidth())
                                                                .ringIntensity(structure.getRingStructure()
                                                                                .getRingIntensity())
                                                                .coreToRingRatio(structure.getRingStructure()
                                                                                .getCoreToRingRatio())
                                                                .build() : null)
                                .irregularParameters(structure.getIrregularStructure() != null
                                                && structure.getIrregularStructure().getIrregularity() != null
                                                                ? IrregularParametersDTO.builder()
                                                                                .irregularity(structure
                                                                                                .getIrregularStructure()
                                                                                                .getIrregularity())
                                                                                .irregularClumpCount(structure
                                                                                                .getIrregularStructure()
                                                                                                .getIrregularClumpCount())
                                                                                .irregularClumpSize(structure
                                                                                                .getIrregularStructure()
                                                                                                .getIrregularClumpSize())
                                                                                .build()
                                                                : null)
                                .starFieldParameters(structure.getStarField() != null ? StarFieldParametersDTO.builder()
                                                .enabled(structure.getStarField().isStarFieldEnabled())
                                                .density(structure.getStarField().getStarDensity())
                                                .maxStarSize(structure.getStarField().getMaxStarSize())
                                                .diffractionSpikes(structure.getStarField().isDiffractionSpikes())
                                                .spikeCount(structure.getStarField().getSpikeCount())
                                                .build() : null)
                                .multiLayerNoiseParameters(structure.getMultiLayerNoise() != null
                                                ? MultiLayerNoiseParametersDTO.builder()
                                                                .enabled(structure.getMultiLayerNoise()
                                                                                .isMultiLayerNoiseEnabled())
                                                                .macroLayerScale(structure.getMultiLayerNoise()
                                                                                .getMacroLayerScale())
                                                                .macroLayerWeight(structure.getMultiLayerNoise()
                                                                                .getMacroLayerWeight())
                                                                .mesoLayerScale(structure.getMultiLayerNoise()
                                                                                .getMesoLayerScale())
                                                                .mesoLayerWeight(structure.getMultiLayerNoise()
                                                                                .getMesoLayerWeight())
                                                                .microLayerScale(structure.getMultiLayerNoise()
                                                                                .getMicroLayerScale())
                                                                .microLayerWeight(structure.getMultiLayerNoise()
                                                                                .getMicroLayerWeight())
                                                                .build()
                                                : null)
                                .colorParameters(structure.getColorConfig() != null ? ColorParametersDTO.builder()
                                                .colorPalette(structure.getColorConfig().getColorPalette())
                                                .spaceBackgroundColor(
                                                                structure.getColorConfig().getSpaceBackgroundColor())
                                                .coreColor(structure.getColorConfig().getCoreColor())
                                                .armColor(structure.getColorConfig().getArmColor())
                                                .outerColor(structure.getColorConfig().getOuterColor())
                                                .build() : null)
                                .build();
        }
}
