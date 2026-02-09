package org.dbs.sbgb.domain.strategy;

import org.dbs.sbgb.domain.constant.GalaxyDefaults;
import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
import org.dbs.sbgb.domain.model.IrregularGalaxyGenerator;
import org.dbs.sbgb.domain.model.parameters.CoreParameters;
import org.dbs.sbgb.domain.model.parameters.IrregularStructureParameters;
import org.springframework.stereotype.Component;

@Component
public class IrregularGeneratorStrategy implements GalaxyGeneratorStrategy {

    @Override
    public GalaxyIntensityCalculator create(GalaxyGenerationContext context) {
        GalaxyParameters parameters = context.getParameters();

        CoreParameters coreParams = parameters.getCoreParameters() != null
                ? parameters.getCoreParameters()
                : CoreParameters.builder()
                        .coreSize(parameters.getCoreSize())
                        .galaxyRadius(parameters.getGalaxyRadius())
                        .build();

        IrregularStructureParameters irregularParams = parameters.getIrregularParameters() != null
                ? parameters.getIrregularParameters()
                : IrregularStructureParameters.builder()
                        .irregularity(parameters.getIrregularity() != null ? parameters.getIrregularity()
                                : GalaxyDefaults.DEFAULT_IRREGULARITY)
                        .clumpCount(parameters.getIrregularClumpCount() != null
                                ? parameters.getIrregularClumpCount()
                                : GalaxyDefaults.DEFAULT_CLUMP_COUNT)
                        .clumpSize(parameters.getIrregularClumpSize() != null
                                ? parameters.getIrregularClumpSize()
                                : GalaxyDefaults.DEFAULT_CLUMP_SIZE)
                        .build();

        return IrregularGalaxyGenerator.builder()
                .width(context.getWidth())
                .height(context.getHeight())
                .noiseGenerator(context.getNoiseGenerator())
                .seed(context.getSeed())
                .coreParameters(coreParams)
                .irregularParameters(irregularParams)
                .build();
    }

    @Override
    public GalaxyType getSupportedType() {
        return GalaxyType.IRREGULAR;
    }
}
