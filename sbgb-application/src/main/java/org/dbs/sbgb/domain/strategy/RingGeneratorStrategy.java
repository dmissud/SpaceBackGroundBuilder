package org.dbs.sbgb.domain.strategy;

import org.dbs.sbgb.domain.constant.GalaxyDefaults;
import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
import org.dbs.sbgb.domain.model.RingGalaxyGenerator;
import org.dbs.sbgb.domain.model.parameters.CoreParameters;
import org.dbs.sbgb.domain.model.parameters.RingStructureParameters;
import org.springframework.stereotype.Component;

@Component
public class RingGeneratorStrategy implements GalaxyGeneratorStrategy {

    @Override
    public GalaxyIntensityCalculator create(GalaxyGenerationContext context) {
        GalaxyParameters parameters = context.getParameters();

        CoreParameters coreParams = parameters.getCoreParameters() != null
                ? parameters.getCoreParameters()
                : CoreParameters.builder()
                        .coreSize(parameters.getCoreSize())
                        .galaxyRadius(parameters.getGalaxyRadius())
                        .build();

        RingStructureParameters ringParams = parameters.getRingParameters() != null
                ? parameters.getRingParameters()
                : RingStructureParameters.builder()
                        .ringRadius(parameters.getRingRadius() != null ? parameters.getRingRadius()
                                : GalaxyDefaults.DEFAULT_RING_RADIUS)
                        .ringWidth(parameters.getRingWidth() != null ? parameters.getRingWidth()
                                : GalaxyDefaults.DEFAULT_RING_WIDTH)
                        .ringIntensity(parameters.getRingIntensity() != null ? parameters.getRingIntensity()
                                : GalaxyDefaults.DEFAULT_RING_INTENSITY)
                        .coreToRingRatio(parameters.getCoreToRingRatio() != null
                                ? parameters.getCoreToRingRatio()
                                : GalaxyDefaults.DEFAULT_CORE_TO_RING_RATIO)
                        .build();

        return RingGalaxyGenerator.builder()
                .width(context.getWidth())
                .height(context.getHeight())
                .noiseGenerator(context.getNoiseGenerator())
                .coreParameters(coreParams)
                .ringParameters(ringParams)
                .build();
    }

    @Override
    public GalaxyType getSupportedType() {
        return GalaxyType.RING;
    }
}
