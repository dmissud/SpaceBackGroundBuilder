package org.dbs.sbgb.domain.service;

import lombok.RequiredArgsConstructor;
import org.dbs.sbgb.common.UseCase;
import org.dbs.sbgb.domain.exception.ImageNameAlreadyExistsException;
import org.dbs.sbgb.domain.mapper.GalaxyStructureMapper;
import org.dbs.sbgb.domain.model.*;
import org.dbs.sbgb.port.in.BuildGalaxyImageUseCase;
import org.dbs.sbgb.port.in.CreateGalaxyImageUseCase;
import org.dbs.sbgb.port.in.FindGalaxyImagesUseCase;
import org.dbs.sbgb.port.in.GalaxyRequestCmd;
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
    private final GalaxyStructureMapper galaxyStructureMapper;

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

        GalaxyStructure structure = galaxyStructureMapper.toGalaxyStructure(galaxyRequestCmd);

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
        GalaxyParameters parameters = galaxyStructureMapper.toGalaxyParameters(cmd);

        // Create color calculator based on colorPalette parameter
        GalaxyColorCalculator colorCalculator = galaxyStructureMapper.createColorCalculator(cmd.getColorParameters());

        GalaxyImageCalculator calculator = new GalaxyImageCalculator.Builder()
                .withWidth(cmd.getWidth())
                .withHeight(cmd.getHeight())
                .withParameters(parameters)
                .withColorCalculator(colorCalculator)
                .build();

        return calculator.create(cmd.getSeed());
    }

    private byte[] convertToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
