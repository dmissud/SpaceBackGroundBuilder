package org.dbs.sbgb.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import org.dbs.sbgb.domain.factory.NoiseGeneratorFactory;
import org.dbs.sbgb.domain.mapper.GalaxyStructureMapper;
import org.dbs.sbgb.domain.model.GalaxyColorCalculator;
import org.dbs.sbgb.domain.model.GalaxyImageRenderer;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.service.BloomApplicator;
import org.dbs.sbgb.domain.service.StarFieldApplicator;
import org.dbs.sbgb.domain.strategy.GalaxyGeneratorFactory;
import org.dbs.sbgb.port.in.GalaxyRequestCmd;
import org.dbs.sbgb.port.out.GalaxyImageComputationPort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

/**
 * Adaptateur de calcul de l'image de la galaxie avec mise en cache Caffeine.
 * La clé de cache est le configHash (hash des paramètres structurants).
 */
@Component
@RequiredArgsConstructor
public class CachedGalaxyImageAdapter implements GalaxyImageComputationPort {

    private final GalaxyStructureMapper galaxyStructureMapper;
    private final GalaxyGeneratorFactory galaxyGeneratorFactory;
    private final NoiseGeneratorFactory noiseGeneratorFactory;
    private final StarFieldApplicator starFieldApplicator;
    private final BloomApplicator bloomApplicator;

    @Override
    @Cacheable(value = "galaxyImage", key = "#p0")
    public BufferedImage computeImage(int configHash, GalaxyRequestCmd cmd) {
        GalaxyParameters parameters = galaxyStructureMapper.toGalaxyParameters(cmd);
        GalaxyColorCalculator colorCalculator = galaxyStructureMapper.createColorCalculator(cmd.getColorParameters());

        GalaxyImageRenderer renderer = new GalaxyImageRenderer.Builder()
                .withWidth(cmd.getWidth())
                .withHeight(cmd.getHeight())
                .withParameters(parameters)
                .withColorCalculator(colorCalculator)
                .withGeneratorFactory(galaxyGeneratorFactory)
                .withNoiseGeneratorFactory(noiseGeneratorFactory)
                .withStarFieldApplicator(starFieldApplicator)
                .withBloomApplicator(bloomApplicator)
                .build();

        return renderer.create(cmd.getSeed());
    }
}
