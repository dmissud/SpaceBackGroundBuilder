package org.dbs.sbgb.domain.strategy;

import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
import org.dbs.sbgb.domain.model.particle.EllipticalParticleIntensityCalculator;
import org.springframework.stereotype.Component;

@Component
public class EllipticalGeneratorStrategy implements GalaxyGeneratorStrategy {

    private static final float PARTICLES_PER_PIXEL = 1.0f / 40.0f;

    @Override
    public GalaxyIntensityCalculator create(GalaxyGenerationContext context) {
        GalaxyParameters parameters = context.getParameters();
        int starCount = Math.max(1000, (int) (context.getWidth() * context.getHeight() * PARTICLES_PER_PIXEL));
        float galaxyRadius = (float) parameters.getCoreParameters().getGalaxyRadius();

        return new EllipticalParticleIntensityCalculator(
                context.getWidth(), context.getHeight(),
                galaxyRadius, starCount,
                parameters.getEllipticalParameters(),
                context.getSeed());
    }

    @Override
    public GalaxyType getSupportedType() {
        return GalaxyType.ELLIPTICAL;
    }
}
