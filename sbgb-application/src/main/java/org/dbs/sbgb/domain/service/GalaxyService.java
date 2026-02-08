package org.dbs.sbgb.domain.service;

import lombok.RequiredArgsConstructor;
import org.dbs.sbgb.common.UseCase;
import org.dbs.sbgb.domain.exception.ImageNameAlreadyExistsException;
import org.dbs.sbgb.domain.model.*;
import org.dbs.sbgb.port.in.*;
import org.dbs.sbgb.port.out.GalaxyImageRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class GalaxyService implements BuildGalaxyImageUseCase, CreateGalaxyImageUseCase, FindGalaxyImagesUseCase {

    private final GalaxyImageRepository galaxyImageRepository;

    @Override
    public List<GalaxyImage> findAllGalaxyImages() {
        return galaxyImageRepository.findAll();
    }

    @Override
    public byte[] buildGalaxyImage(GalaxyRequestCmd galaxyRequestCmd) throws IOException {
        BufferedImage image = generateGalaxyBufferedImage(galaxyRequestCmd);
        return convertToByteArray(image);
    }

    @Override
    public GalaxyImage createGalaxyImage(GalaxyRequestCmd galaxyRequestCmd) throws IOException {
        if (galaxyRequestCmd.getName() == null || galaxyRequestCmd.getName().isBlank()) {
            throw new IllegalArgumentException("Le nom de la galaxie est obligatoire");
        }

        Optional<GalaxyImage> existingImage = galaxyImageRepository.findByName(galaxyRequestCmd.getName());

        if (existingImage.isPresent() && !galaxyRequestCmd.isForceUpdate()) {
            throw new ImageNameAlreadyExistsException(galaxyRequestCmd.getName());
        }

        BufferedImage image = generateGalaxyBufferedImage(galaxyRequestCmd);
        byte[] imageBytes = convertToByteArray(image);

        NoiseParameters noise = galaxyRequestCmd.getNoiseParameters();
        SpiralParameters spiral = galaxyRequestCmd.getSpiralParameters();
        VoronoiParameters voronoi = galaxyRequestCmd.getVoronoiParameters();
        EllipticalParameters elliptical = galaxyRequestCmd.getEllipticalParameters();
        RingParameters ring = galaxyRequestCmd.getRingParameters();
        IrregularParameters irregular = galaxyRequestCmd.getIrregularParameters();
        StarFieldParameters starField = galaxyRequestCmd.getStarFieldParameters();
        MultiLayerNoiseParameters multiLayer = galaxyRequestCmd.getMultiLayerNoiseParameters();
        ColorParameters color = galaxyRequestCmd.getColorParameters();

        GalaxyStructure structure = GalaxyStructure.builder()
                .width(galaxyRequestCmd.getWidth())
                .height(galaxyRequestCmd.getHeight())
                .seed(galaxyRequestCmd.getSeed())
                .galaxyType(galaxyRequestCmd.getGalaxyType())
                .numberOfArms(spiral != null ? defaultIfNull(spiral.numberOfArms(), 2) : 2)
                .armWidth(spiral != null ? defaultIfNull(spiral.armWidth(), 80.0) : 80.0)
                .armRotation(spiral != null ? defaultIfNull(spiral.armRotation(), 4.0) : 4.0)
                .coreSize(defaultIfNull(galaxyRequestCmd.getCoreSize(), 0.05))
                .galaxyRadius(defaultIfNull(galaxyRequestCmd.getGalaxyRadius(), 1500.0))
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
                .warpStrength(galaxyRequestCmd.getWarpStrength())
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

        UUID id = existingImage.map(GalaxyImage::getId).orElse(UUID.randomUUID());

        GalaxyImage galaxyImage = new GalaxyImage();
        galaxyImage.setId(id);
        galaxyImage.setName(galaxyRequestCmd.getName());
        galaxyImage.setDescription(galaxyRequestCmd.getDescription());
        galaxyImage.setNote(existingImage.map(GalaxyImage::getNote).orElse(0));
        galaxyImage.setGalaxyStructure(structure);
        galaxyImage.setImage(imageBytes);

        return galaxyImageRepository.save(galaxyImage);
    }

    private BufferedImage generateGalaxyBufferedImage(GalaxyRequestCmd cmd) {
        GalaxyType galaxyType = parseGalaxyType(cmd.getGalaxyType());

        NoiseParameters noise = cmd.getNoiseParameters();
        SpiralParameters spiral = cmd.getSpiralParameters();
        VoronoiParameters voronoi = cmd.getVoronoiParameters();
        EllipticalParameters elliptical = cmd.getEllipticalParameters();
        RingParameters ring = cmd.getRingParameters();
        IrregularParameters irregular = cmd.getIrregularParameters();
        StarFieldParameters starField = cmd.getStarFieldParameters();
        MultiLayerNoiseParameters multiLayer = cmd.getMultiLayerNoiseParameters();

        GalaxyParameters parameters = GalaxyParameters.builder()
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

        // Create color calculator based on colorPalette parameter
        GalaxyColorCalculator colorCalculator = createColorCalculator(cmd.getColorParameters());

        GalaxyImageCalculator calculator = new GalaxyImageCalculator.Builder()
                .withWidth(cmd.getWidth())
                .withHeight(cmd.getHeight())
                .withParameters(parameters)
                .withColorCalculator(colorCalculator)
                .build();

        return calculator.create(cmd.getSeed());
    }

    private static <T> T defaultIfNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    private GalaxyType parseGalaxyType(String galaxyTypeStr) {
        if (galaxyTypeStr == null || galaxyTypeStr.isBlank()) {
            return GalaxyType.SPIRAL;
        }
        return GalaxyType.valueOf(galaxyTypeStr);
    }

    private GalaxyColorCalculator createColorCalculator(ColorParameters colorParams) {
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
        java.awt.Color spaceBackground = parseColor(colorParams.spaceBackgroundColor());
        java.awt.Color core = parseColor(colorParams.coreColor());
        java.awt.Color arms = parseColor(colorParams.armColor());
        java.awt.Color outer = parseColor(colorParams.outerColor());

        return new DefaultGalaxyColorCalculator(spaceBackground, core, arms, outer);
    }

    private java.awt.Color parseColor(String hex) {
        return java.awt.Color.decode(hex);
    }

    private byte[] convertToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
