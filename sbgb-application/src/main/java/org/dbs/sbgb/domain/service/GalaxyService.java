package org.dbs.sbgb.domain.service;

import lombok.RequiredArgsConstructor;
import org.dbs.sbgb.common.UseCase;
import org.dbs.sbgb.domain.factory.NoiseGeneratorFactory;
import org.dbs.sbgb.domain.mapper.GalaxyStructureMapper;
import org.dbs.sbgb.domain.model.*;
import org.dbs.sbgb.domain.strategy.GalaxyGeneratorFactory;
import org.dbs.sbgb.port.in.BuildGalaxyImageUseCase;
import org.dbs.sbgb.port.in.CreateGalaxyImageUseCase;
import org.dbs.sbgb.port.in.FindGalaxyImagesUseCase;
import org.dbs.sbgb.port.in.GalaxyRequestCmd;
import org.dbs.sbgb.port.out.GalaxyImageRepository;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class GalaxyService implements BuildGalaxyImageUseCase, CreateGalaxyImageUseCase, FindGalaxyImagesUseCase {

    private final GalaxyImageRepository galaxyImageRepository;
    private final GalaxyStructureMapper galaxyStructureMapper;
    private final GalaxyGeneratorFactory galaxyGeneratorFactory;
    private final NoiseGeneratorFactory noiseGeneratorFactory;
    private final StarFieldApplicator starFieldApplicator;
    private final ImageSerializer imageSerializer;
    private final GalaxyImageDuplicationHandler duplicationHandler;

    @Override
    public List<GalaxyImage> findAllGalaxyImages() {
        return galaxyImageRepository.findAll();
    }

    @Override
    public byte[] buildGalaxyImage(GalaxyRequestCmd galaxyRequestCmd) throws IOException {
        BufferedImage image = generateGalaxyBufferedImage(galaxyRequestCmd);
        return imageSerializer.toByteArray(image);
    }

    @Override
    public GalaxyImage createGalaxyImage(GalaxyRequestCmd galaxyRequestCmd) throws IOException {
        UUID id = duplicationHandler.resolveId(galaxyRequestCmd.getName(), galaxyRequestCmd.isForceUpdate());
        int note = duplicationHandler.resolveNote(galaxyRequestCmd.getName());

        BufferedImage image = generateGalaxyBufferedImage(galaxyRequestCmd);
        byte[] imageBytes = imageSerializer.toByteArray(image);
        GalaxyStructure structure = galaxyStructureMapper.toGalaxyStructure(galaxyRequestCmd);

        GalaxyImage galaxyImage = GalaxyImage.builder()
                .id(id)
                .name(galaxyRequestCmd.getName())
                .description(galaxyRequestCmd.getDescription())
                .note(note)
                .galaxyStructure(structure)
                .image(imageBytes)
                .build();

        return galaxyImageRepository.save(galaxyImage);
    }

    private BufferedImage generateGalaxyBufferedImage(GalaxyRequestCmd cmd) {
        GalaxyParameters parameters = galaxyStructureMapper.toGalaxyParameters(cmd);

        // Create color calculator based on colorPalette parameter
        GalaxyColorCalculator colorCalculator = galaxyStructureMapper.createColorCalculator(cmd.getColorParameters());

        GalaxyImageRenderer renderer = new GalaxyImageRenderer.Builder()
                .withWidth(cmd.getWidth())
                .withHeight(cmd.getHeight())
                .withParameters(parameters)
                .withColorCalculator(colorCalculator)
                .withGeneratorFactory(galaxyGeneratorFactory)
                .withNoiseGeneratorFactory(noiseGeneratorFactory)
                .withStarFieldApplicator(starFieldApplicator)
                .build();

        return renderer.create(cmd.getSeed());
    }
}
