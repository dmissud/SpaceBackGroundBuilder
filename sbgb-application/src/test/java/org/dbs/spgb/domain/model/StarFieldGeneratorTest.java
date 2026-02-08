package org.dbs.spgb.domain.model;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;

class StarFieldGeneratorTest {

    @Test
    void shouldReturnOriginalImageWhenStarDensityIsZero() {
        // Given
        BufferedImage originalImage = createBlackImage(1000, 1000);
        StarFieldGenerator generator = StarFieldGenerator.builder()
            .width(1000)
            .height(1000)
            .starDensity(0.0)
            .maxStarSize(4)
            .diffractionSpikes(false)
            .spikeCount(4)
            .seed(12345L)
            .build();

        // When
        BufferedImage result = generator.applyStarField(originalImage);

        // Then
        assertThat(result).isEqualTo(originalImage);
    }

    @Test
    void shouldAddStarsToImage() {
        // Given
        BufferedImage originalImage = createBlackImage(1000, 1000);
        StarFieldGenerator generator = StarFieldGenerator.builder()
            .width(1000)
            .height(1000)
            .starDensity(0.0001)  // Low density for testing
            .maxStarSize(4)
            .diffractionSpikes(false)
            .spikeCount(4)
            .seed(12345L)
            .build();

        // When
        BufferedImage result = generator.applyStarField(originalImage);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getWidth()).isEqualTo(1000);
        assertThat(result.getHeight()).isEqualTo(1000);

        // Check that some pixels are not black (stars were added)
        int nonBlackPixels = countNonBlackPixels(result);
        assertThat(nonBlackPixels).isGreaterThan(0);
    }

    @Test
    void shouldProduceReproducibleResultsWithSameSeed() {
        // Given
        BufferedImage originalImage1 = createBlackImage(1000, 1000);
        BufferedImage originalImage2 = createBlackImage(1000, 1000);

        StarFieldGenerator generator1 = StarFieldGenerator.builder()
            .width(1000)
            .height(1000)
            .starDensity(0.0001)
            .maxStarSize(4)
            .diffractionSpikes(false)
            .spikeCount(4)
            .seed(42L)
            .build();

        StarFieldGenerator generator2 = StarFieldGenerator.builder()
            .width(1000)
            .height(1000)
            .starDensity(0.0001)
            .maxStarSize(4)
            .diffractionSpikes(false)
            .spikeCount(4)
            .seed(42L)
            .build();

        // When
        BufferedImage result1 = generator1.applyStarField(originalImage1);
        BufferedImage result2 = generator2.applyStarField(originalImage2);

        // Then
        assertThat(countNonBlackPixels(result1)).isEqualTo(countNonBlackPixels(result2));
    }

    @Test
    void shouldProduceDifferentResultsWithDifferentSeeds() {
        // Given
        BufferedImage originalImage1 = createBlackImage(1000, 1000);
        BufferedImage originalImage2 = createBlackImage(1000, 1000);

        StarFieldGenerator generator1 = StarFieldGenerator.builder()
            .width(1000)
            .height(1000)
            .starDensity(0.001)
            .maxStarSize(4)
            .diffractionSpikes(false)
            .spikeCount(4)
            .seed(42L)
            .build();

        StarFieldGenerator generator2 = StarFieldGenerator.builder()
            .width(1000)
            .height(1000)
            .starDensity(0.001)
            .maxStarSize(4)
            .diffractionSpikes(false)
            .spikeCount(4)
            .seed(999L)
            .build();

        // When
        BufferedImage result1 = generator1.applyStarField(originalImage1);
        BufferedImage result2 = generator2.applyStarField(originalImage2);

        // Then - should have different number of non-black pixels due to different star positions
        int count1 = countNonBlackPixels(result1);
        int count2 = countNonBlackPixels(result2);

        // Allow for some variation but they should be different
        assertThat(Math.abs(count1 - count2)).isGreaterThan(10);
    }

    @Test
    void shouldAddMoreStarsWithHigherDensity() {
        // Given
        BufferedImage lowDensityImage = createBlackImage(1000, 1000);
        BufferedImage highDensityImage = createBlackImage(1000, 1000);

        StarFieldGenerator lowDensity = StarFieldGenerator.builder()
            .width(1000)
            .height(1000)
            .starDensity(0.0001)
            .maxStarSize(4)
            .diffractionSpikes(false)
            .spikeCount(4)
            .seed(12345L)
            .build();

        StarFieldGenerator highDensity = StarFieldGenerator.builder()
            .width(1000)
            .height(1000)
            .starDensity(0.001)  // 10x higher
            .maxStarSize(4)
            .diffractionSpikes(false)
            .spikeCount(4)
            .seed(12345L)
            .build();

        // When
        BufferedImage lowResult = lowDensity.applyStarField(lowDensityImage);
        BufferedImage highResult = highDensity.applyStarField(highDensityImage);

        // Then
        int lowCount = countNonBlackPixels(lowResult);
        int highCount = countNonBlackPixels(highResult);

        assertThat(highCount).isGreaterThan(lowCount * 2);  // At least 2x more pixels
    }

    private BufferedImage createBlackImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return image;
    }

    private int countNonBlackPixels(BufferedImage image) {
        int count = 0;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb, true);
                if (color.getRed() > 0 || color.getGreen() > 0 || color.getBlue() > 0) {
                    count++;
                }
            }
        }
        return count;
    }
}
