package org.dbs.sbgb.domain.model;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * Post-processor that applies a bloom/glow effect to bright regions of the image.
 * 
 * Algorithm:
 * 1. Extract bright pixels above threshold
 * 2. Apply Gaussian blur to create bloom mask
 * 3. Composite bloom mask additively onto original image
 */
@Slf4j
@Builder
public class BloomPostProcessor {

    @Builder.Default
    private final int bloomRadius = 10;
    
    @Builder.Default
    private final double bloomIntensity = 0.5;
    
    @Builder.Default
    private final double bloomThreshold = 0.5;

    /**
     * Apply bloom effect to the image.
     * 
     * @param source Original image
     * @return Image with bloom effect applied
     */
    public BufferedImage apply(BufferedImage source) {
        if (bloomIntensity <= 0.0) {
            return source;
        }

        log.info("Applying bloom: radius={}, intensity={}, threshold={}", bloomRadius, bloomIntensity, bloomThreshold);

        int width = source.getWidth();
        int height = source.getHeight();

        // Step 1: Extract bright pixels above threshold
        BufferedImage brightMask = extractBrightPixels(source, bloomThreshold);

        // Step 2: Apply Gaussian blur to create bloom effect
        BufferedImage blurred = applyGaussianBlur(brightMask, bloomRadius);

        // Step 3: Composite bloom onto original image
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int originalRGB = source.getRGB(x, y);
                int bloomRGB = blurred.getRGB(x, y);

                int origR = (originalRGB >> 16) & 0xFF;
                int origG = (originalRGB >> 8) & 0xFF;
                int origB = originalRGB & 0xFF;

                int bloomR = (bloomRGB >> 16) & 0xFF;
                int bloomG = (bloomRGB >> 8) & 0xFF;
                int bloomB = bloomRGB & 0xFF;

                // Additive blending with intensity control
                int finalR = clamp((int) (origR + bloomR * bloomIntensity));
                int finalG = clamp((int) (origG + bloomG * bloomIntensity));
                int finalB = clamp((int) (origB + bloomB * bloomIntensity));

                int finalRGB = (finalR << 16) | (finalG << 8) | finalB;
                result.setRGB(x, y, finalRGB);
            }
        }

        return result;
    }

    /**
     * Extract pixels that are brighter than the threshold.
     */
    private BufferedImage extractBrightPixels(BufferedImage source, double threshold) {
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage mask = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int thresholdValue = (int) (threshold * 255);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = source.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                // Check if pixel intensity is above threshold
                int intensity = (r + g + b) / 3;
                if (intensity > thresholdValue) {
                    mask.setRGB(x, y, rgb);
                } else {
                    mask.setRGB(x, y, 0);  // Black
                }
            }
        }

        return mask;
    }

    /**
     * Apply Gaussian blur using ConvolveOp.
     */
    private BufferedImage applyGaussianBlur(BufferedImage source, int radius) {
        if (radius < 1) {
            return source;
        }

        // Create Gaussian kernel
        int kernelSize = radius * 2 + 1;
        float[] kernelData = createGaussianKernel(kernelSize);
        Kernel kernel = new Kernel(kernelSize, kernelSize, kernelData);

        ConvolveOp convolve = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        
        // ConvolveOp requires a destination image
        BufferedImage dest = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        convolve.filter(source, dest);
        
        return dest;
    }

    /**
     * Create 2D Gaussian kernel for blur.
     */
    private float[] createGaussianKernel(int size) {
        float[] kernel = new float[size * size];
        double sigma = size / 3.0;  // Standard deviation
        double twoSigmaSquare = 2.0 * sigma * sigma;
        double sigmaRoot = Math.sqrt(twoSigmaSquare * Math.PI);
        double total = 0.0;
        int center = size / 2;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int dx = x - center;
                int dy = y - center;
                double distance = dx * dx + dy * dy;
                double value = Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
                kernel[y * size + x] = (float) value;
                total += value;
            }
        }

        // Normalize kernel
        for (int i = 0; i < kernel.length; i++) {
            kernel[i] /= total;
        }

        return kernel;
    }

    /**
     * Clamp value to 0-255 range.
     */
    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
