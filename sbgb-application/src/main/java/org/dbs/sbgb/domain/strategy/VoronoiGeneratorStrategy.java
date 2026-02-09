package org.dbs.sbgb.domain.strategy;

import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
import org.dbs.sbgb.domain.model.VoronoiClusterGalaxyGenerator;
import org.springframework.stereotype.Component;

@Component
public class VoronoiGeneratorStrategy implements GalaxyGeneratorStrategy {

    @Override
    public GalaxyIntensityCalculator create(GalaxyGenerationContext context) {
        GalaxyParameters parameters = context.getParameters();

        return VoronoiClusterGalaxyGenerator.builder()
                .width(context.getWidth())
                .height(context.getHeight())
                .noiseGenerator(context.getNoiseGenerator())
                .seed(context.getSeed())
                .coreParameters(parameters.getCoreParameters())
                .voronoiParameters(parameters.getVoronoiParameters())
                .build();
    }

    @Override
    public GalaxyType getSupportedType() {
        return GalaxyType.VORONOI_CLUSTER;
    }
}
