package org.dbs.sbgb.domain.strategy;

import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.GalaxyType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GalaxyGeneratorFactory {

    private final Map<GalaxyType, GalaxyGeneratorStrategy> strategies;

    public GalaxyGeneratorFactory(List<GalaxyGeneratorStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(GalaxyGeneratorStrategy::getSupportedType, Function.identity()));
    }

    public GalaxyIntensityCalculator create(GalaxyType type, GalaxyGenerationContext context) {
        return Optional.ofNullable(strategies.get(type))
                .map(strategy -> strategy.create(context))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported galaxy type: " + type));
    }
}
