package org.dbs.sbgb.domain.service;

import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
import org.dbs.sbgb.domain.model.parameters.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;

class BloomApplicatorTest {

    private BloomApplicator bloomApplicator;
    private BufferedImage testImage;

    @BeforeEach
    void setUp() {
        bloomApplicator = new BloomApplicator();
        testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        // Paint a bright white center pixel to trigger bloom
        testImage.setRGB(50, 50, 0xFFFFFF);
    }

    @Test
    void shouldReturnOriginalImageWhenBloomDisabled() {
        GalaxyParameters params = parametersWithBloom(BloomParameters.builder()
                .enabled(false)
                .build());

        BufferedImage result = bloomApplicator.applyIfEnabled(testImage, params);

        assertThat(result).isSameAs(testImage);
    }

    @Test
    void shouldReturnNewImageWhenBloomEnabled() {
        GalaxyParameters params = parametersWithBloom(BloomParameters.builder()
                .enabled(true)
                .bloomRadius(3)
                .bloomIntensity(0.5)
                .bloomThreshold(0.3)
                .build());

        BufferedImage result = bloomApplicator.applyIfEnabled(testImage, params);

        assertThat(result).isNotNull();
        assertThat(result.getWidth()).isEqualTo(testImage.getWidth());
        assertThat(result.getHeight()).isEqualTo(testImage.getHeight());
    }

    @Test
    void shouldNotModifyImageWhenIntensityIsZero() {
        GalaxyParameters params = parametersWithBloom(BloomParameters.builder()
                .enabled(true)
                .bloomRadius(5)
                .bloomIntensity(0.0)
                .bloomThreshold(0.3)
                .build());

        BufferedImage result = bloomApplicator.applyIfEnabled(testImage, params);

        assertThat(result).isSameAs(testImage);
    }

    @Test
    void shouldProduceBrighterPixelsAroundBrightCenter() {
        // All black image except one very bright pixel
        BufferedImage allBlack = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        allBlack.setRGB(25, 25, 0xFFFFFF);

        GalaxyParameters params = parametersWithBloom(BloomParameters.builder()
                .enabled(true)
                .bloomRadius(5)
                .bloomIntensity(1.0)
                .bloomThreshold(0.1)
                .build());

        BufferedImage result = bloomApplicator.applyIfEnabled(allBlack, params);

        // Neighbor pixels should be brighter than zero due to bloom spread
        int neighborPixel = result.getRGB(25, 27);
        int neighborR = (neighborPixel >> 16) & 0xFF;
        assertThat(neighborR).isGreaterThan(0);
    }

    private GalaxyParameters parametersWithBloom(BloomParameters bloomParameters) {
        return GalaxyParameters.builder()
                .galaxyType(GalaxyType.SPIRAL)
                .coreParameters(CoreParameters.builder().coreSize(0.05).galaxyRadius(1500.0).build())
                .noiseTextureParameters(NoiseTextureParameters.builder().build())
                .bloomParameters(bloomParameters)
                .build();
    }
}
