package org.dbs.sbgb.domain.strategy;

import org.dbs.sbgb.domain.model.PerlinGenerator;
import org.dbs.sbgb.domain.constant.GalaxyDefaults;
import org.dbs.sbgb.domain.model.IrregularGalaxyGenerator;
import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
import org.springframework.stereotype.Component;

@Component
public class IrregularGeneratorStrategy implements GalaxyGeneratorStrategy {

        @Override
        public GalaxyIntensityCalculator create(GalaxyGenerationContext context) {
                GalaxyParameters parameters = context.getParameters();
                return IrregularGalaxyGenerator.builder()
                                .width(context.getWidth())
                                .height(context.getHeight())
                                .noiseGenerator(context.getNoiseGenerator())
                                .seed(context.getSeed())
                                .coreSize(parameters.getCoreSize())
                                .galaxyRadius(parameters.getGalaxyRadius())
                                .irregularity(parameters.getIrregularity() != null ? parameters.getIrregularity()
                                                : GalaxyDefaults.DEFAULT_IRREGULARITY)
                                .clumpCount(parameters.getIrregularClumpCount() != null
                                                ? parameters.getIrregularClumpCount()
                                                : GalaxyDefaults.DEFAULT_CLUMP_COUNT)
                                .clumpSize(parameters.getIrregularClumpSize() != null
                                                ? parameters.getIrregularClumpSize()
                                                : GalaxyDefaults.DEFAULT_CLUMP_SIZE)
                                .build();
        }

        @Override
        public GalaxyType getSupportedType() {
                return GalaxyType.IRREGULAR;
        }
}
