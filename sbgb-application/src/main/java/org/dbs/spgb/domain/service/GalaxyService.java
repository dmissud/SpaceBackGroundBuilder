package org.dbs.spgb.domain.service;

import lombok.RequiredArgsConstructor;
import org.dbs.spgb.common.UseCase;
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
        BufferedImage image = generateGalaxyBufferedImage(galaxyRequestCmd);
        byte[] imageBytes = convertToByteArray(image);

        GalaxyStructure structure = GalaxyStructure.builder()
                .width(galaxyRequestCmd.getWidth())
                .height(galaxyRequestCmd.getHeight())
                .seed(galaxyRequestCmd.getSeed())
                .numberOfArms(galaxyRequestCmd.getNumberOfArms())
                .armWidth(galaxyRequestCmd.getArmWidth())
                .armRotation(galaxyRequestCmd.getArmRotation())
                .coreSize(galaxyRequestCmd.getCoreSize())
                .galaxyRadius(galaxyRequestCmd.getGalaxyRadius())
                .noiseOctaves(galaxyRequestCmd.getNoiseOctaves())
                .noisePersistence(galaxyRequestCmd.getNoisePersistence())
                .noiseLacunarity(galaxyRequestCmd.getNoiseLacunarity())
                .noiseScale(galaxyRequestCmd.getNoiseScale())
                .spaceBackgroundColor(galaxyRequestCmd.getSpaceBackgroundColor())
                .coreColor(galaxyRequestCmd.getCoreColor())
                .armColor(galaxyRequestCmd.getArmColor())
                .outerColor(galaxyRequestCmd.getOuterColor())
                .build();

        GalaxyImage galaxyImage = new GalaxyImage();
        galaxyImage.setId(UUID.randomUUID());
        galaxyImage.setName(galaxyRequestCmd.getName());
        galaxyImage.setDescription(galaxyRequestCmd.getDescription());
        galaxyImage.setNote(0);
        galaxyImage.setGalaxyStructure(structure);
        galaxyImage.setImage(imageBytes);

        return galaxyImageRepository.save(galaxyImage);
    }

    private BufferedImage generateGalaxyBufferedImage(GalaxyRequestCmd cmd) {
        GalaxyParameters parameters = GalaxyParameters.builder()
                .numberOfArms(cmd.getNumberOfArms())
                .armWidth(cmd.getArmWidth())
                .armRotation(cmd.getArmRotation())
                .coreSize(cmd.getCoreSize())
                .galaxyRadius(cmd.getGalaxyRadius())
                .noiseOctaves(cmd.getNoiseOctaves())
                .noisePersistence(cmd.getNoisePersistence())
                .noiseLacunarity(cmd.getNoiseLacunarity())
                .noiseScale(cmd.getNoiseScale())
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

    private java.awt.Color parseColor(String hex) {
        return java.awt.Color.decode(hex);
    }

    private byte[] convertToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
