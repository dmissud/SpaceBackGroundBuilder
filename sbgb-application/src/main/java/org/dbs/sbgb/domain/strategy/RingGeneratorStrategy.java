package org.dbs.sbgb.domain.strategy;

import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
import org.dbs.sbgb.domain.model.particle.RingParticleIntensityCalculator;
import org.springframework.stereotype.Component;

@Component
public class RingGeneratorStrategy implements GalaxyGeneratorStrategy {

    @Override
    public GalaxyIntensityCalculator create(GalaxyGenerationContext context) {
        GalaxyParameters parameters = context.getParameters();

        return new RingParticleIntensityCalculator(
                context.getWidth(), context.getHeight(),
                parameters.getCoreParameters(),
                parameters.getRingParameters(),
                context.getSeed());
    }

    @Override
    public GalaxyType getSupportedType() {
        return GalaxyType.RING;
    }
}
