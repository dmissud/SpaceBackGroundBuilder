package org.dbs.spgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;

/**
 * Adapter to make MultiLayerNoiseGenerator compatible with PerlinGenerator interface.
 * This allows multi-layer noise to be used transparently in galaxy generators.
 */
public class MultiLayerNoiseAdapter extends PerlinGenerator {

    private final MultiLayerNoiseGenerator multiLayerGenerator;

    public MultiLayerNoiseAdapter(MultiLayerNoiseGenerator multiLayerGenerator) {
        super(Interpolation.LINEAR, FadeFunction.NONE);
        this.multiLayerGenerator = multiLayerGenerator;
    }

    @Override
    public void createNoisePipeline(long seed, int width, int height, int octaves, double persistence, double lacunarity, double scale, NoiseType noiseType) {
        // No-op: multi-layer generator is already initialized
    }

    @Override
    public double scaleNoiseNormalizedValue(int x, int y) {
        return multiLayerGenerator.evaluate(x, y);
    }

    @Override
    public void performNormalization() {
        // No-op: multi-layer generator already normalizes internally
    }
}
