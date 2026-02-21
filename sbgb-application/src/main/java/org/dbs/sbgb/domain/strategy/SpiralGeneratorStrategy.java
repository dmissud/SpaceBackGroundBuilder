package org.dbs.sbgb.domain.strategy;

import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
import org.dbs.sbgb.domain.model.SpiralGalaxyGenerator;
import org.springframework.stereotype.Component;

@Component
public class SpiralGeneratorStrategy implements GalaxyGeneratorStrategy {

    @Override
    public GalaxyIntensityCalculator create(GalaxyGenerationContext context) {
        GalaxyParameters parameters = context.getParameters();

        return SpiralGalaxyGenerator.builder()
                .width(context.getWidth())
                .height(context.getHeight())
                .seed(context.getSeed())
                .coreParameters(parameters.getCoreParameters())
                .spiralParameters(parameters.getSpiralParameters())
                .build();
    }

    @Override
    public GalaxyType getSupportedType() {
        return GalaxyType.SPIRAL;
    }
}
