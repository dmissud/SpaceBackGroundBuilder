package org.dbs.sbgb.domain.strategy;

import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.GalaxyType;

public interface GalaxyGeneratorStrategy {
    GalaxyIntensityCalculator create(GalaxyGenerationContext context);

    GalaxyType getSupportedType();
}
