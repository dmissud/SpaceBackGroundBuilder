package org.dbs.sbgb.domain.model;

import de.articdive.jnoise.pipeline.JNoise;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

/**
 * Grille de bruit normalisée : résultat du calcul Perlin après scan de normalisation.
 * Peut être mise en cache pour éviter de recalculer la grille quand seuls les
 * paramètres cosmétiques changent.
 */
public record NormalizedNoiseGrid(JNoise noisePipeline, double minVal, double maxVal, int width, int height) {

    /** Retourne la valeur normalisée dans [0, 1] pour le pixel (x, y). */
    public double normalizedValueAt(int x, int y) {
        double raw = noisePipeline.evaluateNoise(x * 1.0 / width, y * 1.0 / height);
        return (raw - minVal) / (maxVal - minVal);
    }

    /** Applique un calculateur de couleurs sur la grille et retourne l'image résultante. */
    public BufferedImage renderWithColors(NoiseColorCalculator colorCalculator) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setBackground(colorCalculator.getBackGroundColor());
        g2d.clearRect(0, 0, width, height);
        g2d.dispose();

        IntStream.range(0, width).parallel().forEach(x -> {
            for (int y = 0; y < height; y++) {
                img.setRGB(x, y, colorCalculator.calculateNoiseColor(normalizedValueAt(x, y)).getRGB());
            }
        });

        return img;
    }
}
