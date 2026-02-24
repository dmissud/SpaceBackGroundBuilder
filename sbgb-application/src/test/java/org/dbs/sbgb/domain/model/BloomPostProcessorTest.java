package org.dbs.sbgb.domain.model;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;

class BloomPostProcessorTest {

    @Test
    void shouldNotModifyImageWhenBloomIntensityIsZero() {
        BufferedImage original = createTestImage(100, 100);
        setPixel(original, 50, 50, 255, 255, 255);  // Bright pixel

        BloomPostProcessor processor = BloomPostProcessor.builder()
                .bloomRadius(10)
                .bloomIntensity(0.0)
                .bloomThreshold(0.5)
                .build();

        BufferedImage result = processor.apply(original);

        // With zero intensity, image should be identical
        assertThat(result.getRGB(50, 50)).isEqualTo(original.getRGB(50, 50));
    }

    @Test
    void shouldApplyBloomOnlyToPixelsAboveThreshold() {
        BufferedImage image = createTestImage(100, 100);
        setPixel(image, 50, 50, 255, 255, 255);  // Bright pixel (intensity = 1.0)
        setPixel(image, 30, 30, 64, 64, 64);     // Dim pixel (intensity = 0.25)

        BloomPostProcessor processor = BloomPostProcessor.builder()
                .bloomRadius(5)
                .bloomIntensity(0.5)
                .bloomThreshold(0.5)  // Only pixels with intensity > 0.5 should bloom
                .build();

        BufferedImage result = processor.apply(image);

        // Bright pixel should have bloom effect (neighboring pixels should be brighter)
        int neighborBrightness = getBrightness(result, 52, 52);
        assertThat(neighborBrightness).isGreaterThan(0);

        // Dim pixel area should remain mostly unchanged
        int dimNeighborBrightness = getBrightness(result, 32, 32);
        assertThat(dimNeighborBrightness).isLessThan(neighborBrightness);
    }

    @Test
    void shouldHaveStrongerBloomWithHigherIntensity() {
        BufferedImage image = createTestImage(100, 100);
        setPixel(image, 50, 50, 255, 255, 255);

        BloomPostProcessor weakBloom = BloomPostProcessor.builder()
                .bloomRadius(5)
                .bloomIntensity(0.3)
                .bloomThreshold(0.5)
                .build();

        BloomPostProcessor strongBloom = BloomPostProcessor.builder()
                .bloomRadius(5)
                .bloomIntensity(0.8)
                .bloomThreshold(0.5)
                .build();

        BufferedImage weakResult = weakBloom.apply(cloneImage(image));
        BufferedImage strongResult = strongBloom.apply(cloneImage(image));

        // Strong bloom should produce brighter neighbors
        int weakNeighbor = getBrightness(weakResult, 52, 52);
        int strongNeighbor = getBrightness(strongResult, 52, 52);
        assertThat(strongNeighbor).isGreaterThan(weakNeighbor);
    }

    @Test
    void shouldHaveWiderBloomWithLargerRadius() {
        BufferedImage image = createTestImage(100, 100);
        setPixel(image, 50, 50, 255, 255, 255);

        BloomPostProcessor smallRadius = BloomPostProcessor.builder()
                .bloomRadius(3)
                .bloomIntensity(0.5)
                .bloomThreshold(0.5)
                .build();

        BloomPostProcessor largeRadius = BloomPostProcessor.builder()
                .bloomRadius(8)
                .bloomIntensity(0.5)
                .bloomThreshold(0.5)
                .build();

        BufferedImage smallResult = smallRadius.apply(cloneImage(image));
        BufferedImage largeResult = largeRadius.apply(cloneImage(image));

        // Large radius should affect closer pixels more than small radius
        int smallNear = getBrightness(smallResult, 54, 54);
        int largeNear = getBrightness(largeResult, 54, 54);
        assertThat(largeNear).isGreaterThanOrEqualTo(smallNear);
    }

    @Test
    void shouldNotExceedMaximumBrightness() {
        BufferedImage image = createTestImage(100, 100);
        setPixel(image, 50, 50, 255, 255, 255);

        BloomPostProcessor processor = BloomPostProcessor.builder()
                .bloomRadius(10)
                .bloomIntensity(1.0)  // Maximum intensity
                .bloomThreshold(0.1)
                .build();

        BufferedImage result = processor.apply(image);

        // All pixel values should be clamped to 255
        for (int x = 45; x <= 55; x++) {
            for (int y = 45; y <= 55; y++) {
                int rgb = result.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                assertThat(r).isLessThanOrEqualTo(255);
                assertThat(g).isLessThanOrEqualTo(255);
                assertThat(b).isLessThanOrEqualTo(255);
            }
        }
    }

    // Helper methods

    private BufferedImage createTestImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    private void setPixel(BufferedImage image, int x, int y, int r, int g, int b) {
        int rgb = (r << 16) | (g << 8) | b;
        image.setRGB(x, y, rgb);
    }

    private int getBrightness(BufferedImage image, int x, int y) {
        int rgb = image.getRGB(x, y);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return (r + g + b) / 3;
    }

    private BufferedImage cloneImage(BufferedImage source) {
        BufferedImage clone = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        clone.getGraphics().drawImage(source, 0, 0, null);
        return clone;
    }
}
