package org.dbs.sbgb.domain.strategy;

import org.dbs.sbgb.domain.model.GalaxyGenerator;
import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
import org.dbs.sbgb.domain.model.parameters.CoreParameters;
import org.dbs.sbgb.domain.model.parameters.SpiralStructureParameters;
import org.springframework.stereotype.Component;

@Component
public class SpiralGeneratorStrategy implements GalaxyGeneratorStrategy {

    @Override
    public GalaxyIntensityCalculator create(GalaxyGenerationContext context) {
        GalaxyParameters parameters = context.getParameters();

        CoreParameters coreParams = parameters.getCoreParameters() != null
                ? parameters.getCoreParameters()
                : CoreParameters.builder()
                        .coreSize(parameters.getCoreSize())
                        .galaxyRadius(parameters.getGalaxyRadius())
                        .build();

        SpiralStructureParameters spiralParams = parameters.getSpiralParameters() != null
                ? parameters.getSpiralParameters()
                : SpiralStructureParameters.builder()
                        .numberOfArms(parameters.getNumberOfArms())
                        .armWidth(parameters.getArmWidth())
                        .armRotation(parameters.getArmRotation())
                        .build();

        return GalaxyGenerator.builder()
                .width(context.getWidth())
                .height(context.getHeight())
                .noiseGenerator(context.getNoiseGenerator())
                .coreParameters(coreParams)
                .spiralParameters(spiralParams)
                .build();
    }

    @Override
    public GalaxyType getSupportedType() {
        return GalaxyType.SPIRAL;
    }
}
