package cucumber.steps;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import org.dbs.sbgb.domain.model.NormalizedNoiseGrid;
import org.dbs.sbgb.domain.model.NoiseImageCalculator;
import org.dbs.sbgb.domain.model.NoiseType;
import org.dbs.sbgb.domain.model.PerlinGenerator;
import org.dbs.sbgb.port.in.ImageRequestCmd;
import org.dbs.sbgb.port.out.NoiseGridComputationPort;

import java.util.List;
import java.util.stream.IntStream;

/** Stub de NoiseGridComputationPort pour les tests : calcul direct sans cache. */
public class NoiseGridComputationPortStub implements NoiseGridComputationPort {

    @Override
    public NormalizedNoiseGrid computeSingleLayerGrid(int configHash, ImageRequestCmd.SizeCmd sizeCmd) {
        PerlinGenerator generator = new PerlinGenerator(
                NoiseImageCalculator.DEFAULT_INTERPOLATION,
                NoiseImageCalculator.DEFAULT_FADE_FUNCTION);
        return generator.computeAndNormalize(
                sizeCmd.getSeed(), sizeCmd.getWidth(), sizeCmd.getHeight(),
                sizeCmd.getOctaves(), sizeCmd.getPersistence(), sizeCmd.getLacunarity(),
                sizeCmd.getScale(), NoiseType.valueOf(sizeCmd.getNoiseType() != null ? sizeCmd.getNoiseType() : "FBM"));
    }

    @Override
    public List<NormalizedNoiseGrid> computeMultiLayerGrids(int configHash, ImageRequestCmd.SizeCmd sizeCmd) {
        if (sizeCmd.getLayers() == null || sizeCmd.getLayers().isEmpty()) {
            return List.of(computeSingleLayerGrid(configHash, sizeCmd));
        }
        return sizeCmd.getLayers().stream()
                .filter(ImageRequestCmd.LayerCmd::isEnabled)
                .map(layer -> {
                    PerlinGenerator gen = new PerlinGenerator(Interpolation.COSINE, FadeFunction.CUBIC_POLY);
                    return gen.computeAndNormalize(
                            sizeCmd.getSeed() + layer.getSeedOffset(),
                            sizeCmd.getWidth(), sizeCmd.getHeight(),
                            layer.getOctaves(), layer.getPersistence(), layer.getLacunarity(),
                            layer.getScale(), NoiseType.valueOf(layer.getNoiseType() != null ? layer.getNoiseType() : "FBM"));
                })
                .toList();
    }
}
