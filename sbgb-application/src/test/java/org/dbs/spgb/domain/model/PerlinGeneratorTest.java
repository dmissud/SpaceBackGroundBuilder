package org.dbs.spgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PerlinGeneratorTest {

    @Test
    void shouldCreateFbmNoise() {
        PerlinGenerator generator = new PerlinGenerator(Interpolation.LINEAR, FadeFunction.NONE);
        generator.createNoisePipeline(123L, 100, 100, 3, 0.5, 2.0, 1.0, NoiseType.FBM);
        generator.performNormalization();

        double val = generator.scaleNoiseNormalizedValue(50, 50);
        assertThat(val).isBetween(0.0, 1.0);
    }

    @Test
    void shouldCreateRidgedNoise() {
        PerlinGenerator generator = new PerlinGenerator(Interpolation.LINEAR, FadeFunction.NONE);
        // This should fail until implemented
        generator.createNoisePipeline(123L, 100, 100, 3, 0.5, 2.0, 1.0, NoiseType.RIDGED);
        generator.performNormalization();

        double val = generator.scaleNoiseNormalizedValue(50, 50);
        assertThat(val).isBetween(0.0, 1.0);
    }
}
