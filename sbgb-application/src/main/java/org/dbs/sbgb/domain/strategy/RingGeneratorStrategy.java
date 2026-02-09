package org.dbs.sbgb.domain.strategy;

import org.dbs.sbgb.domain.model.PerlinGenerator;
import org.dbs.sbgb.domain.constant.GalaxyDefaults;
import org.dbs.sbgb.domain.model.RingGalaxyGenerator;
import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
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
                                .coreSize(parameters.getCoreSize())
                                .galaxyRadius(parameters.getGalaxyRadius())
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
        }

        @Override
        public GalaxyType getSupportedType() {
                return GalaxyType.RING;
        }
}
