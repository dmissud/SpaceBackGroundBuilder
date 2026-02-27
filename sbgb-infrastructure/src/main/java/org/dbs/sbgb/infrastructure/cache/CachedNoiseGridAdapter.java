package org.dbs.sbgb.infrastructure.cache;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import org.dbs.sbgb.domain.model.NormalizedNoiseGrid;
import org.dbs.sbgb.domain.model.NoiseImageCalculator;
import org.dbs.sbgb.domain.model.NoiseType;
import org.dbs.sbgb.domain.model.PerlinGenerator;
import org.dbs.sbgb.port.in.ImageRequestCmd;
import org.dbs.sbgb.port.out.NoiseGridComputationPort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adaptateur de calcul de grilles de bruit avec mise en cache Caffeine.
 * La clé de cache est le configHash : même params structurants = même grille.
 */
@Component
public class CachedNoiseGridAdapter implements NoiseGridComputationPort {

    @Override
    @Cacheable(value = "noiseGrid", key = "#p0")
    public NormalizedNoiseGrid computeSingleLayerGrid(int configHash, ImageRequestCmd.SizeCmd sizeCmd) {
        PerlinGenerator generator = new PerlinGenerator(
                NoiseImageCalculator.DEFAULT_INTERPOLATION,
                NoiseImageCalculator.DEFAULT_FADE_FUNCTION);
        return generator.computeAndNormalize(
                sizeCmd.getSeed(), sizeCmd.getWidth(), sizeCmd.getHeight(),
                sizeCmd.getOctaves(), sizeCmd.getPersistence(), sizeCmd.getLacunarity(),
                sizeCmd.getScale(), resolveNoiseType(sizeCmd.getNoiseType()));
    }

    @Override
    @Cacheable(value = "noiseGrid", key = "'multi_' + #p0")
    public List<NormalizedNoiseGrid> computeMultiLayerGrids(int configHash, ImageRequestCmd.SizeCmd sizeCmd) {
        if (sizeCmd.getLayers() == null || sizeCmd.getLayers().isEmpty()) {
            return List.of(computeSingleLayerGrid(configHash, sizeCmd));
        }
        return sizeCmd.getLayers().stream()
                .filter(ImageRequestCmd.LayerCmd::isEnabled)
                .map(layer -> computeLayerGrid(sizeCmd, layer))
                .toList();
    }

    private NormalizedNoiseGrid computeLayerGrid(ImageRequestCmd.SizeCmd sizeCmd, ImageRequestCmd.LayerCmd layer) {
        PerlinGenerator generator = new PerlinGenerator(Interpolation.COSINE, FadeFunction.CUBIC_POLY);
        return generator.computeAndNormalize(
                sizeCmd.getSeed() + layer.getSeedOffset(),
                sizeCmd.getWidth(), sizeCmd.getHeight(),
                layer.getOctaves(), layer.getPersistence(), layer.getLacunarity(),
                layer.getScale(), resolveNoiseType(layer.getNoiseType()));
    }

    private NoiseType resolveNoiseType(String noiseType) {
        return noiseType != null ? NoiseType.valueOf(noiseType) : NoiseType.FBM;
    }
}
