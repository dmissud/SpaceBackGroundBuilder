package org.dbs.sbgb.domain.strategy;

import org.dbs.sbgb.domain.model.EllipticalGalaxyGenerator;
import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
import org.springframework.stereotype.Component;

@Component
public class EllipticalGeneratorStrategy implements GalaxyGeneratorStrategy {

    @Override
    public GalaxyIntensityCalculator create(GalaxyGenerationContext context) {
        GalaxyParameters parameters = context.getParameters();

        return EllipticalGalaxyGenerator.builder()
                .width(context.getWidth())
                .height(context.getHeight())
                .seed(context.getSeed())
                .coreParameters(parameters.getCoreParameters())
                .ellipticalParameters(parameters.getEllipticalParameters())
                .build();
    }

    @Override
    public GalaxyType getSupportedType() {
        return GalaxyType.ELLIPTICAL;
    }
}
