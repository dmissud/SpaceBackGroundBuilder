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

    // Cache for the last generated image results to avoid re-calculating during
    // save
    private GalaxyRequestCmd lastRequest;
    private byte[] lastBytes;

    @Override
    public List<GalaxyImage> findAllGalaxyImages() {
        return galaxyImageRepository.findAll();
    }

    @Override
    public byte[] buildGalaxyImage(GalaxyRequestCmd galaxyRequestCmd) throws IOException {
        BufferedImage image = generateGalaxyBufferedImage(galaxyRequestCmd);
        byte[] bytes = imageSerializer.toByteArray(image);

        // Update cache
        this.lastRequest = galaxyRequestCmd;
        this.lastBytes = bytes;

        return bytes;
    }

    @Override
    public GalaxyImage createGalaxyImage(GalaxyRequestCmd galaxyRequestCmd) throws IOException {
        UUID id = duplicationHandler.resolveId(galaxyRequestCmd.getName(), galaxyRequestCmd.isForceUpdate());
        int note = duplicationHandler.resolveNote(galaxyRequestCmd.getName());

        byte[] imageBytes;
        if (isSameImagery(galaxyRequestCmd, lastRequest) && lastBytes != null) {
            imageBytes = lastBytes;
        } else {
            BufferedImage image = generateGalaxyBufferedImage(galaxyRequestCmd);
            imageBytes = imageSerializer.toByteArray(image);
            // Optional: update cache here too?
            this.lastRequest = galaxyRequestCmd;
            this.lastBytes = imageBytes;
        }

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

    private boolean isSameImagery(GalaxyRequestCmd cmd1, GalaxyRequestCmd cmd2) {
        if (cmd1 == null || cmd2 == null)
            return false;

        // We compare everything except metadata (name, description, forceUpdate)
        // A simple way since we have @Data is to compare a "stripped" version or just
        // relevant fields.
        // For safety and simplicity, we check the core parameters.
        return cmd1.getSeed() == cmd2.getSeed() &&
                cmd1.getWidth() == cmd2.getWidth() &&
                cmd1.getHeight() == cmd2.getHeight() &&
                java.util.Objects.equals(cmd1.getGalaxyType(), cmd2.getGalaxyType()) &&
                java.util.Objects.equals(cmd1.getCoreSize(), cmd2.getCoreSize()) &&
                java.util.Objects.equals(cmd1.getGalaxyRadius(), cmd2.getGalaxyRadius()) &&
                Double.compare(cmd1.getWarpStrength(), cmd2.getWarpStrength()) == 0 &&
                java.util.Objects.equals(cmd1.getNoiseParameters(), cmd2.getNoiseParameters()) &&
                java.util.Objects.equals(cmd1.getSpiralParameters(), cmd2.getSpiralParameters()) &&
                java.util.Objects.equals(cmd1.getVoronoiParameters(), cmd2.getVoronoiParameters()) &&
                java.util.Objects.equals(cmd1.getEllipticalParameters(), cmd2.getEllipticalParameters()) &&
                java.util.Objects.equals(cmd1.getRingParameters(), cmd2.getRingParameters()) &&
                java.util.Objects.equals(cmd1.getIrregularParameters(), cmd2.getIrregularParameters()) &&
                java.util.Objects.equals(cmd1.getStarFieldParameters(), cmd2.getStarFieldParameters()) &&
                java.util.Objects.equals(cmd1.getMultiLayerNoiseParameters(), cmd2.getMultiLayerNoiseParameters()) &&
                java.util.Objects.equals(cmd1.getColorParameters(), cmd2.getColorParameters());
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
