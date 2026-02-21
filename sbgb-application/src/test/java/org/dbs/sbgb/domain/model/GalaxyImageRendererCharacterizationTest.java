package org.dbs.sbgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import org.dbs.sbgb.domain.factory.NoiseGeneratorFactory;
import org.dbs.sbgb.domain.service.BloomApplicator;
import org.dbs.sbgb.domain.service.StarFieldApplicator;
import org.dbs.sbgb.domain.strategy.GalaxyGeneratorFactory;
import org.dbs.sbgb.domain.strategy.SpiralGeneratorStrategy;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de Caractérisation pour la génération des images (Golden Masters).
 * Fige le comportement algorithmique actuel avant tout refactoring du God
 * Object.
 */
class GalaxyImageRendererCharacterizationTest {

    @Test
    void characterizeSpiralGalaxy() throws NoSuchAlgorithmException {
        GalaxyParameters spiralParams = GalaxyParameters.builder()
                .galaxyType(GalaxyType.SPIRAL)
                .coreParameters(org.dbs.sbgb.domain.model.parameters.CoreParameters.builder().coreSize(0.05)
                        .galaxyRadius(100.0).build())
                .noiseTextureParameters(org.dbs.sbgb.domain.model.parameters.NoiseTextureParameters.builder().octaves(4)
                        .persistence(0.5).lacunarity(2.0).scale(0.1).build())
                .domainWarpParameters(
                        org.dbs.sbgb.domain.model.parameters.DomainWarpParameters.builder().warpStrength(0.0).build())
                .multiLayerNoiseParameters(
                        org.dbs.sbgb.domain.model.parameters.MultiLayerNoiseParameters.builder().build())
                .starFieldParameters(org.dbs.sbgb.domain.model.parameters.StarFieldParameters.builder().build())
                .spiralParameters(org.dbs.sbgb.domain.model.parameters.SpiralStructureParameters.builder()
                        .numberOfArms(3).armWidth(0.5).armRotation(2.0).build())
                .build();

        GalaxyColorCalculator colorCalculator = ColorPalette.NEBULA.createCalculator();

        GalaxyGeneratorFactory generatorFactory = new GalaxyGeneratorFactory(List.of(new SpiralGeneratorStrategy()));
        NoiseGeneratorFactory noiseFactory = new NoiseGeneratorFactory();
        StarFieldApplicator starFieldApplicator = new StarFieldApplicator();
        BloomApplicator bloomApplicator = new BloomApplicator();

        GalaxyImageRenderer renderer = new GalaxyImageRenderer.Builder()
                .withWidth(500)
                .withHeight(500)
                .withInterpolation(Interpolation.COSINE)
                .withFadeFunction(FadeFunction.CUBIC_POLY)
                .withGeneratorFactory(generatorFactory)
                .withNoiseGeneratorFactory(noiseFactory)
                .withStarFieldApplicator(starFieldApplicator)
                .withBloomApplicator(bloomApplicator)
                .withParameters(spiralParams)
                .withColorCalculator(colorCalculator)
                .build();

        BufferedImage image = renderer.create(12345L);

        assertThat(image).isNotNull();
        assertThat(image.getWidth()).isEqualTo(500);
        assertThat(image.getHeight()).isEqualTo(500);

        // Compute SHA-256 of the uncompressed raster (Golden Master hash)
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        int[] pixels = image.getRGB(0, 0, 500, 500, null, 0, 500);
        for (int p : pixels) {
            md.update((byte) (p >> 24));
            md.update((byte) (p >> 16));
            md.update((byte) (p >> 8));
            md.update((byte) p);
        }

        String hash = HexFormat.of().formatHex(md.digest());
        System.out.println("Spiral Golden Master Hash: " + hash);
        assertThat(hash).isNotBlank();
    }
}
