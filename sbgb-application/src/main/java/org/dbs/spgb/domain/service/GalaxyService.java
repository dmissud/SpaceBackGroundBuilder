package org.dbs.spgb.domain.service;

import lombok.RequiredArgsConstructor;
import org.dbs.spgb.common.UseCase;
import org.dbs.spgb.domain.exception.ImageNameAlreadyExistsException;
import org.dbs.spgb.domain.model.*;
import org.dbs.spgb.port.in.BuildGalaxyImageUseCase;
import org.dbs.spgb.port.in.CreateGalaxyImageUseCase;
import org.dbs.spgb.port.in.FindGalaxyImagesUseCase;
import org.dbs.spgb.port.in.GalaxyRequestCmd;
import org.dbs.spgb.port.out.GalaxyImageRepository;

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

        GalaxyStructure structure = GalaxyStructure.builder()
                .width(galaxyRequestCmd.getWidth())
                .height(galaxyRequestCmd.getHeight())
                .seed(galaxyRequestCmd.getSeed())
                .galaxyType(galaxyRequestCmd.getGalaxyType())
                .numberOfArms(defaultIfNull(galaxyRequestCmd.getNumberOfArms(), 2))
                .armWidth(defaultIfNull(galaxyRequestCmd.getArmWidth(), 80.0))
                .armRotation(defaultIfNull(galaxyRequestCmd.getArmRotation(), 4.0))
                .coreSize(defaultIfNull(galaxyRequestCmd.getCoreSize(), 0.05))
                .galaxyRadius(defaultIfNull(galaxyRequestCmd.getGalaxyRadius(), 1500.0))
                .noiseOctaves(galaxyRequestCmd.getNoiseOctaves())
                .noisePersistence(galaxyRequestCmd.getNoisePersistence())
                .noiseLacunarity(galaxyRequestCmd.getNoiseLacunarity())
                .noiseScale(galaxyRequestCmd.getNoiseScale())
                .clusterCount(galaxyRequestCmd.getClusterCount())
                .clusterSize(galaxyRequestCmd.getClusterSize())
                .clusterConcentration(galaxyRequestCmd.getClusterConcentration())
                .spaceBackgroundColor(galaxyRequestCmd.getSpaceBackgroundColor())
                .coreColor(galaxyRequestCmd.getCoreColor())
                .armColor(galaxyRequestCmd.getArmColor())
                .outerColor(galaxyRequestCmd.getOuterColor())
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

        GalaxyParameters parameters = GalaxyParameters.builder()
                .galaxyType(galaxyType)
                .numberOfArms(defaultIfNull(cmd.getNumberOfArms(), 2))
                .armWidth(defaultIfNull(cmd.getArmWidth(), 80.0))
                .armRotation(defaultIfNull(cmd.getArmRotation(), 4.0))
                .coreSize(defaultIfNull(cmd.getCoreSize(), 0.05))
                .galaxyRadius(defaultIfNull(cmd.getGalaxyRadius(), 1500.0))
                .noiseOctaves(cmd.getNoiseOctaves())
                .noisePersistence(cmd.getNoisePersistence())
                .noiseLacunarity(cmd.getNoiseLacunarity())
                .noiseScale(cmd.getNoiseScale())
                .clusterCount(cmd.getClusterCount())
                .clusterSize(cmd.getClusterSize())
                .clusterConcentration(cmd.getClusterConcentration())
                .build();

        // Parse colors from hex strings
        java.awt.Color spaceBackground = parseColor(cmd.getSpaceBackgroundColor());
        java.awt.Color core = parseColor(cmd.getCoreColor());
        java.awt.Color arms = parseColor(cmd.getArmColor());
        java.awt.Color outer = parseColor(cmd.getOuterColor());

        GalaxyColorCalculator colorCalculator = new DefaultGalaxyColorCalculator(
                spaceBackground, core, arms, outer
        );

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

    private java.awt.Color parseColor(String hex) {
        return java.awt.Color.decode(hex);
    }

    private byte[] convertToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
