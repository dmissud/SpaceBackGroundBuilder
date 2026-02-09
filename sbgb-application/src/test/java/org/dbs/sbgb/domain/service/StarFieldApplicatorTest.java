package org.dbs.sbgb.domain.service;

import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StarFieldApplicator")
class StarFieldApplicatorTest {

    private StarFieldApplicator applicator;

    @BeforeEach
    void setUp() {
        applicator = new StarFieldApplicator();
    }

    @Test
    @DisplayName("should return original image when star density is zero")
    void shouldReturnOriginalImageWhenStarDensityIsZero() {
        // Given
        BufferedImage originalImage = createTestImage(500, 500, Color.BLACK);
        GalaxyParameters parameters = GalaxyParameters.builder()
                .galaxyType(org.dbs.sbgb.domain.model.GalaxyType.SPIRAL)
                .numberOfArms(2)
                .armWidth(80.0)
                .armRotation(4.0)
                .coreSize(0.05)
                .galaxyRadius(1500.0)
                .noiseOctaves(4)
                .noisePersistence(0.5)
                .noiseLacunarity(2.0)
                .noiseScale(200.0)
                .starDensity(0.0)
                .build();

        // When
        BufferedImage result = applicator.applyIfEnabled(originalImage, parameters, 12345L);

        // Then
        assertThat(result).isSameAs(originalImage);
    }

    @Test
    @DisplayName("should apply star field when density is greater than zero")
    void shouldApplyStarFieldWhenDensityIsPositive() {
        // Given
        BufferedImage originalImage = createTestImage(500, 500, Color.BLACK);
        GalaxyParameters parameters = GalaxyParameters.builder()
                .galaxyType(org.dbs.sbgb.domain.model.GalaxyType.SPIRAL)
                .numberOfArms(2)
                .armWidth(80.0)
                .armRotation(4.0)
                .coreSize(0.05)
                .galaxyRadius(1500.0)
                .noiseOctaves(4)
                .noisePersistence(0.5)
                .noiseLacunarity(2.0)
                .noiseScale(200.0)
                .starDensity(0.001)
                .maxStarSize(4)
                .diffractionSpikes(false)
                .spikeCount(4)
                .build();

        // When
        BufferedImage result = applicator.applyIfEnabled(originalImage, parameters, 12345L);

        // Then
        assertThat(result).isNotSameAs(originalImage);
        assertThat(result.getWidth()).isEqualTo(originalImage.getWidth());
        assertThat(result.getHeight()).isEqualTo(originalImage.getHeight());
        assertThat(hasWhitePixels(result)).isTrue();
    }

    @Test
    @DisplayName("should produce different star fields for different seeds")
    void shouldProduceDifferentStarFieldsForDifferentSeeds() {
        // Given
        BufferedImage image1 = createTestImage(500, 500, Color.BLACK);
        BufferedImage image2 = createTestImage(500, 500, Color.BLACK);
        GalaxyParameters parameters = GalaxyParameters.builder()
                .galaxyType(org.dbs.sbgb.domain.model.GalaxyType.SPIRAL)
                .numberOfArms(2)
                .armWidth(80.0)
                .armRotation(4.0)
                .coreSize(0.05)
                .galaxyRadius(1500.0)
                .noiseOctaves(4)
                .noisePersistence(0.5)
                .noiseLacunarity(2.0)
                .noiseScale(200.0)
                .starDensity(0.001)
                .maxStarSize(4)
                .build();

        // When
        BufferedImage result1 = applicator.applyIfEnabled(image1, parameters, 111L);
        BufferedImage result2 = applicator.applyIfEnabled(image2, parameters, 222L);

        // Then
        int whitePixelCount1 = countWhitePixels(result1);
        int whitePixelCount2 = countWhitePixels(result2);

        // Star positions should differ, so pixel counts at same positions should differ
        assertThat(imagesAreDifferent(result1, result2)).isTrue();
    }

    @Test
    @DisplayName("should produce reproducible star field for same seed")
    void shouldProduceReproducibleStarField() {
        // Given
        BufferedImage image1 = createTestImage(500, 500, Color.BLACK);
        BufferedImage image2 = createTestImage(500, 500, Color.BLACK);
        GalaxyParameters parameters = GalaxyParameters.builder()
                .galaxyType(org.dbs.sbgb.domain.model.GalaxyType.SPIRAL)
                .numberOfArms(2)
                .armWidth(80.0)
                .armRotation(4.0)
                .coreSize(0.05)
                .galaxyRadius(1500.0)
                .noiseOctaves(4)
                .noisePersistence(0.5)
                .noiseLacunarity(2.0)
                .noiseScale(200.0)
                .starDensity(0.001)
                .maxStarSize(4)
                .build();

        long seed = 12345L;

        // When
        BufferedImage result1 = applicator.applyIfEnabled(image1, parameters, seed);
        BufferedImage result2 = applicator.applyIfEnabled(image2, parameters, seed);

        // Then
        assertThat(imagesAreIdentical(result1, result2)).isTrue();
    }

    @Test
    @DisplayName("should apply diffraction spikes when enabled")
    void shouldApplyDiffractionSpikesWhenEnabled() {
        // Given
        BufferedImage originalImage = createTestImage(500, 500, Color.BLACK);
        GalaxyParameters parameters = GalaxyParameters.builder()
                .galaxyType(org.dbs.sbgb.domain.model.GalaxyType.SPIRAL)
                .numberOfArms(2)
                .armWidth(80.0)
                .armRotation(4.0)
                .coreSize(0.05)
                .galaxyRadius(1500.0)
                .noiseOctaves(4)
                .noisePersistence(0.5)
                .noiseLacunarity(2.0)
                .noiseScale(200.0)
                .starDensity(0.002)
                .maxStarSize(8)
                .diffractionSpikes(true)
                .spikeCount(6)
                .build();

        // When
        BufferedImage result = applicator.applyIfEnabled(originalImage, parameters, 12345L);

        // Then
        assertThat(result).isNotNull();
        assertThat(hasWhitePixels(result)).isTrue();
    }

    // Helper methods

    private BufferedImage createTestImage(int width, int height, Color backgroundColor) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return image;
    }

    private boolean hasWhitePixels(BufferedImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color pixelColor = new Color(image.getRGB(x, y), true);
                if (pixelColor.getRed() > 100 || pixelColor.getGreen() > 100 || pixelColor.getBlue() > 100) {
                    return true;
                }
            }
        }
        return false;
    }

    private int countWhitePixels(BufferedImage image) {
        int count = 0;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color pixelColor = new Color(image.getRGB(x, y), true);
                if (pixelColor.getRed() > 100 || pixelColor.getGreen() > 100 || pixelColor.getBlue() > 100) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean imagesAreDifferent(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return true;
        }

        int differenceCount = 0;
        for (int x = 0; x < img1.getWidth(); x++) {
            for (int y = 0; y < img1.getHeight(); y++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    differenceCount++;
                }
            }
        }

        // Images should have at least some different pixels
        return differenceCount > 10;
    }

    private boolean imagesAreIdentical(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return false;
        }

        for (int x = 0; x < img1.getWidth(); x++) {
            for (int y = 0; y < img1.getHeight(); y++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    return false;
                }
            }
        }

        return true;
    }
}
