package org.dbs.sbgb.domain.strategy;

import org.dbs.sbgb.domain.model.PerlinGenerator;
import org.dbs.sbgb.domain.constant.GalaxyDefaults;
import org.dbs.sbgb.domain.model.GalaxyGenerator;
import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
import org.dbs.sbgb.domain.strategy.GalaxyGenerationContext;
import org.springframework.stereotype.Component;

@Component
public class SpiralGeneratorStrategy implements GalaxyGeneratorStrategy {

    @Override
    public GalaxyIntensityCalculator create(GalaxyGenerationContext context) {
        GalaxyParameters parameters = context.getParameters();
        return GalaxyGenerator.builder()
                .width(context.getWidth())
                .height(context.getHeight())
                .noiseGenerator(context.getNoiseGenerator())
                .numberOfArms(parameters.getNumberOfArms())
                .armWidth(parameters.getArmWidth())
                .armRotation(parameters.getArmRotation())
                .coreSize(parameters.getCoreSize())
                .galaxyRadius(parameters.getGalaxyRadius())
                .build();
    }

    @Override
    public GalaxyType getSupportedType() {
        return GalaxyType.SPIRAL;
    }
}
