package org.dbs.sbgb.domain.strategy;

import org.dbs.sbgb.domain.model.GalaxyGenerator;
import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
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
                .coreParameters(parameters.getCoreParameters())
                .spiralParameters(parameters.getSpiralParameters())
                .build();
    }

    @Override
    public GalaxyType getSupportedType() {
        return GalaxyType.SPIRAL;
    }
}
