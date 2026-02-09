package org.dbs.sbgb.domain.strategy;

import org.dbs.sbgb.domain.model.PerlinGenerator;
import org.dbs.sbgb.domain.constant.GalaxyDefaults;
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
                                .noiseGenerator(context.getNoiseGenerator())
                                .coreSize(parameters.getCoreSize())
                                .galaxyRadius(parameters.getGalaxyRadius())
                                .sersicIndex(parameters.getSersicIndex() != null ? parameters.getSersicIndex()
                                                : GalaxyDefaults.DEFAULT_SERSIC_INDEX)
                                .axisRatio(parameters.getAxisRatio() != null ? parameters.getAxisRatio()
                                                : GalaxyDefaults.DEFAULT_AXIS_RATIO)
                                .orientationAngle(parameters.getOrientationAngle() != null
                                                ? parameters.getOrientationAngle()
                                                : GalaxyDefaults.DEFAULT_ORIENTATION_ANGLE)
                                .build();
        }

        @Override
        public GalaxyType getSupportedType() {
                return GalaxyType.ELLIPTICAL;
        }
}
