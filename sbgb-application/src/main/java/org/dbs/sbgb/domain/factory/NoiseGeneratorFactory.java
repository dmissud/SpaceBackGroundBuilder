package org.dbs.sbgb.domain.factory;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.model.*;
import org.springframework.stereotype.Component;

/**
 * Factory for creating noise generators based on galaxy parameters.
 * Handles the creation of either single-layer Perlin noise or multi-layer noise.
 */
@Slf4j
@Component
public class NoiseGeneratorFactory {

    /**
     * Create a noise generator based on the parameters configuration.
     *
     * @param parameters galaxy parameters containing noise configuration
     * @param seed random seed for noise generation
     * @param width image width
     * @param height image height
     * @param interpolation interpolation function for Perlin noise
     * @param fadeFunction fade function for Perlin noise
     * @return configured PerlinGenerator (may wrap MultiLayerNoiseGenerator)
     */
    public PerlinGenerator createNoiseGenerator(
            GalaxyParameters parameters,
            long seed,
            int width,
            int height,
            Interpolation interpolation,
            FadeFunction fadeFunction) {

        if (parameters.getMultiLayerNoiseParameters() != null && parameters.getMultiLayerNoiseParameters().isEnabled()) {
            return createMultiLayerNoise(parameters, seed, width, height, interpolation, fadeFunction);
        } else {
            return createSingleLayerNoise(parameters, seed, width, height, interpolation, fadeFunction);
        }
    }

    private PerlinGenerator createMultiLayerNoise(
            GalaxyParameters parameters,
            long seed,
            int width,
            int height,
            Interpolation interpolation,
            FadeFunction fadeFunction) {

        log.debug("Creating multi-layer noise generator with seed={}", seed);

        MultiLayerNoiseGenerator multiLayerNoise = MultiLayerNoiseGenerator.builder()
                .seed(seed)
                .width(width)
                .height(height)
                .interpolation(interpolation)
                .fadeFunction(fadeFunction)
                .noiseType(NoiseType.FBM)
                .macroScale(parameters.getMultiLayerNoiseParameters().getMacroLayerScale())
                .macroWeight(parameters.getMultiLayerNoiseParameters().getMacroLayerWeight())
                .mesoScale(parameters.getMultiLayerNoiseParameters().getMesoLayerScale())
                .mesoWeight(parameters.getMultiLayerNoiseParameters().getMesoLayerWeight())
                .microScale(parameters.getMultiLayerNoiseParameters().getMicroLayerScale())
                .microWeight(parameters.getMultiLayerNoiseParameters().getMicroLayerWeight())
                .build();

        multiLayerNoise.initialize();

        return new MultiLayerNoiseAdapter(multiLayerNoise);
    }

    private PerlinGenerator createSingleLayerNoise(
            GalaxyParameters parameters,
            long seed,
            int width,
            int height,
            Interpolation interpolation,
            FadeFunction fadeFunction) {

        log.debug("Creating single-layer Perlin noise with seed={}", seed);

        PerlinGenerator noiseGenerator = new PerlinGenerator(interpolation, fadeFunction);
        noiseGenerator.createNoisePipeline(
                seed,
                width,
                height,
                parameters.getNoiseTextureParameters().getOctaves(),
                parameters.getNoiseTextureParameters().getPersistence(),
                parameters.getNoiseTextureParameters().getLacunarity(),
                parameters.getNoiseTextureParameters().getScale(),
                NoiseType.FBM);
        noiseGenerator.performNormalization();

        return noiseGenerator;
    }
}
