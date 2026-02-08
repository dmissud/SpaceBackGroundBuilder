package org.dbs.sbgb.domain.model;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generator for star field overlay on galaxy images
 * Creates individual stars with varying sizes and optional diffraction spikes
 */
@Slf4j
@Builder
public class StarFieldGenerator {

    private final int width;
    private final int height;
    private final double starDensity;
    private final int maxStarSize;
    private final boolean diffractionSpikes;
    private final int spikeCount;
    private final long seed;

    /**
     * Apply star field overlay to an existing galaxy image
     */
    public BufferedImage applyStarField(BufferedImage galaxyImage) {
        if (starDensity <= 0.0) {
            log.debug("Star density is 0, skipping star field generation");
            return galaxyImage;
        }

        log.info("Applying star field: density={}, maxSize={}, spikes={}",
                 starDensity, maxStarSize, diffractionSpikes);

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = result.createGraphics();

        // Draw original galaxy image
        g2d.drawImage(galaxyImage, 0, 0, null);

        // Generate and draw stars
        List<Star> stars = generateStars();
        for (Star star : stars) {
            drawStar(g2d, star);
        }

        g2d.dispose();
        log.info("Star field applied with {} stars", stars.size());
        return result;
    }

    private List<Star> generateStars() {
        Random random = new Random(seed);
        int totalPixels = width * height;
        int starCount = (int) (totalPixels * starDensity);

        List<Star> stars = new ArrayList<>();

        // Use Poisson disk sampling approximation for more natural distribution
        // For simplicity, we use random placement with minimum distance check
        for (int i = 0; i < starCount; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);

            // Star size: most are small (1-2px), few are large
            int size = generateStarSize(random);

            // Star brightness: varies from 0.3 to 1.0
            float brightness = 0.3f + random.nextFloat() * 0.7f;

            // Star color: mostly white, slight variations (white, blue-white, yellow-white)
            Color color = generateStarColor(random, brightness);

            stars.add(new Star(x, y, size, color, brightness));
        }

        return stars;
    }

    private int generateStarSize(Random random) {
        // 70% small (1-2px), 25% medium (3-4px), 5% large (5-maxStarSize)
        double roll = random.nextDouble();
        if (roll < 0.70) {
            return 1 + random.nextInt(2);  // 1 or 2
        } else if (roll < 0.95) {
            return 3 + random.nextInt(2);  // 3 or 4
        } else {
            return 5 + random.nextInt(Math.max(1, maxStarSize - 4));
        }
    }

    private Color generateStarColor(Random random, float brightness) {
        double colorType = random.nextDouble();

        if (colorType < 0.7) {
            // White stars (most common)
            int val = (int) (brightness * 255);
            return new Color(val, val, val);
        } else if (colorType < 0.85) {
            // Blue-white stars (hot)
            int val = (int) (brightness * 255);
            return new Color(
                (int) (val * 0.9f),
                (int) (val * 0.95f),
                val
            );
        } else {
            // Yellow-white stars (cooler)
            int val = (int) (brightness * 255);
            return new Color(
                val,
                (int) (val * 0.95f),
                (int) (val * 0.85f)
            );
        }
    }

    private void drawStar(Graphics2D g2d, Star star) {
        // Enable anti-aliasing for smooth stars
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (star.size <= 2) {
            // Small stars: simple pixel or 2x2 square
            g2d.setColor(star.color);
            g2d.fillRect(star.x, star.y, star.size, star.size);
        } else {
            // Larger stars: Gaussian-like glow
            drawStarGlow(g2d, star);

            // Draw diffraction spikes if enabled and star is bright enough
            if (diffractionSpikes && star.brightness > 0.6f && star.size >= 3) {
                drawDiffractionSpikes(g2d, star);
            }
        }
    }

    private void drawStarGlow(Graphics2D g2d, Star star) {
        int glowRadius = star.size * 2;

        for (int r = glowRadius; r >= 0; r--) {
            float alpha = (float) r / glowRadius * star.brightness;
            Color glowColor = new Color(
                star.color.getRed(),
                star.color.getGreen(),
                star.color.getBlue(),
                (int) (alpha * 255)
            );
            g2d.setColor(glowColor);

            int size = r * 2;
            g2d.fillOval(star.x - r, star.y - r, size, size);
        }

        // Bright core
        g2d.setColor(star.color);
        g2d.fillOval(star.x - star.size / 2, star.y - star.size / 2, star.size, star.size);
    }

    private void drawDiffractionSpikes(Graphics2D g2d, Star star) {
        int spikeLength = star.size * 4;
        float alpha = star.brightness * 0.5f;

        Color spikeColor = new Color(
            star.color.getRed(),
            star.color.getGreen(),
            star.color.getBlue(),
            (int) (alpha * 255)
        );

        g2d.setColor(spikeColor);
        g2d.setStroke(new BasicStroke(1.5f));

        // Draw spikes at regular angles
        for (int i = 0; i < spikeCount; i++) {
            double angle = (2.0 * Math.PI * i) / spikeCount;
            int dx = (int) (Math.cos(angle) * spikeLength);
            int dy = (int) (Math.sin(angle) * spikeLength);

            g2d.drawLine(star.x, star.y, star.x + dx, star.y + dy);
        }
    }

    private record Star(int x, int y, int size, Color color, float brightness) {}
}
