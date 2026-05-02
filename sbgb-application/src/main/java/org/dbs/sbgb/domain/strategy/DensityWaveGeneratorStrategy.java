package org.dbs.sbgb.domain.strategy;

import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.GalaxyType;
import org.dbs.sbgb.domain.model.densitywave.DensityWaveGalaxyParams;
import org.dbs.sbgb.domain.model.densitywave.DensityWaveIntensityCalculator;
import org.springframework.stereotype.Component;

@Component
public class DensityWaveGeneratorStrategy implements GalaxyGeneratorStrategy {

    private static final float DEFAULT_GALAXY_RADIUS = 15000.0f;
    private static final int DEFAULT_STAR_COUNT = 60000;

    @Override
    public GalaxyIntensityCalculator create(GalaxyGenerationContext context) {
        DensityWaveGalaxyParams params = context.getParameters().getDensityWaveParams();
        if (params == null) {
            params = DensityWaveGalaxyParams.defaultParams(DEFAULT_GALAXY_RADIUS, DEFAULT_STAR_COUNT);
        }
        return new DensityWaveIntensityCalculator(
                context.getWidth(), context.getHeight(), context.getSeed(), params);
    }

    @Override
    public GalaxyType getSupportedType() {
        return GalaxyType.DENSITY_WAVE;
    }
}
