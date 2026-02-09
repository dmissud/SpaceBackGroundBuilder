package org.dbs.sbgb.domain.strategy;

import org.dbs.sbgb.domain.model.PerlinGenerator;
import org.dbs.sbgb.domain.constant.GalaxyDefaults;
import org.dbs.sbgb.domain.model.VoronoiClusterGalaxyGenerator;
import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
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
                                .coreSize(parameters.getCoreSize())
                                .galaxyRadius(parameters.getGalaxyRadius())
                                .clusterCount(parameters.getClusterCount() != null ? parameters.getClusterCount()
                                                : GalaxyDefaults.DEFAULT_CLUSTER_COUNT)
                                .clusterSize(parameters.getClusterSize() != null ? parameters.getClusterSize()
                                                : GalaxyDefaults.DEFAULT_CLUSTER_SIZE)
                                .clusterConcentration(
                                                parameters.getClusterConcentration() != null
                                                                ? parameters.getClusterConcentration()
                                                                : GalaxyDefaults.DEFAULT_CLUSTER_CONCENTRATION)
                                .build();
        }

        @Override
        public GalaxyType getSupportedType() {
                return GalaxyType.VORONOI_CLUSTER;
        }
}
