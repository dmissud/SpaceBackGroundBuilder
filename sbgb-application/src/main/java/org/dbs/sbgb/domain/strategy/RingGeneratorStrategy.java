package org.dbs.sbgb.domain.strategy;

import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
import org.dbs.sbgb.domain.model.RingGalaxyGenerator;
import org.springframework.stereotype.Component;

@Component
public class RingGeneratorStrategy implements GalaxyGeneratorStrategy {

    @Override
    public GalaxyIntensityCalculator create(GalaxyGenerationContext context) {
        GalaxyParameters parameters = context.getParameters();

        return RingGalaxyGenerator.builder()
                .width(context.getWidth())
                .height(context.getHeight())
                .noiseGenerator(context.getNoiseGenerator())
                .coreParameters(parameters.getCoreParameters())
                .ringParameters(parameters.getRingParameters())
                .build();
    }

    @Override
    public GalaxyType getSupportedType() {
        return GalaxyType.RING;
    }
}
