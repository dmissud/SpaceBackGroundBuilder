package org.dbs.sbgb.domain.strategy;

import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import de.articdive.jnoise.core.api.functions.Interpolation;
import org.dbs.sbgb.domain.model.PerlinGenerator;
import lombok.Builder;
import lombok.Getter;
import org.dbs.sbgb.domain.model.GalaxyParameters;

@Getter
@Builder
public class GalaxyGenerationContext {
    private final int width;
    private final int height;
    private final PerlinGenerator noiseGenerator;
    private final long seed;
    private final GalaxyParameters parameters;
}
