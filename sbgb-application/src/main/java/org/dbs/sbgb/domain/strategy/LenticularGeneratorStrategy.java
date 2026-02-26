package org.dbs.sbgb.domain.strategy;

import org.dbs.sbgb.domain.model.LenticularGalaxyGenerator;
import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
import org.springframework.stereotype.Component;

@Component
public class LenticularGeneratorStrategy implements GalaxyGeneratorStrategy {

    @Override
    public GalaxyIntensityCalculator create(GalaxyGenerationContext context) {
        GalaxyParameters parameters = context.getParameters();

        return LenticularGalaxyGenerator.builder()
                .width(context.getWidth())
                .height(context.getHeight())
                .seed(context.getSeed())
                .coreParameters(parameters.getCoreParameters())
                .lenticularParameters(parameters.getLenticularParameters())
                .build();
    }

    @Override
    public GalaxyType getSupportedType() {
        return GalaxyType.LENTICULAR;
    }
}
