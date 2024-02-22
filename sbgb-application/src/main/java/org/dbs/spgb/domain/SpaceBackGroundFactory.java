package org.dbs.spgb.domain;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import de.articdive.jnoise.pipeline.JNoise;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

@Slf4j
public class BuildSpaceBackGround {

    private final int WIDTH;
    public final int HEIGHT;

    public BuildSpaceBackGround(int width, int height) {
        WIDTH = width;
        HEIGHT = height;
    }

    public static BufferedImage createSpaceBackground(long seed) {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();

        setGraphicsBgColor(img, g2d);

        JNoise noisePipeline = createNoisePipeline(seed);

        double[] noiseValues = getMinMaxNoiseValues(noisePipeline);

        double minVal = noiseValues[0];
        double maxVal = noiseValues[1];

        updateGraphicsColors(g2d, noisePipeline, minVal, maxVal);
    }

    private void setGraphicsBgColor(BufferedImage img, Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 102));
        g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
    }

    private JNoise createNoisePipeline(long seed) {
        return JNoise.newBuilder().perlin(seed, Interpolation.COSINE, FadeFunction.CUBIC_POLY)
                .scale(100)
                .clamp(0.0, 3.0)
                .build();
    }

    private double[] getMinMaxNoiseValues(JNoise noisePipeline) {
        double maxVal = 0;
        double minVal = 1.0;
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                double noiseVal = scaleNoiseValue(noisePipeline, x, y);
                if (noiseVal > maxVal) maxVal = noiseVal;
                if (noiseVal < minVal) minVal = noiseVal;
            }
        }
        return new double[]{minVal, maxVal};
    }

    private double scaleNoiseValue(JNoise noisePipeline, int x, int y) {
        return noisePipeline.evaluateNoise(x / (WIDTH * 1.0), y / (HEIGHT * 1.0));
    }

    private void updateGraphicsColors(Graphics2D g2d, JNoise noisePipeline, double minVal, double maxVal) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                double noiseVal = scaleNoiseValue(noisePipeline, x, y);
                noiseVal = normalizeNoiseValue(noiseVal, minVal, maxVal);
                Color newColor = calculateColor(noiseVal);
                g2d.setColor(newColor);
                g2d.drawLine(x, y, x, y);
            }
        }
    }

    private double normalizeNoiseValue(double noiseVal, double minVal, double maxVal) {
        return (noiseVal - minVal) / (maxVal - minVal);
    }

    private Color calculateColor(double noiseVal) {
        double black_R = 0, black_G = 0, black_B = 0;
        double orange_R = 255, orange_G = 165, orange_B = 0;
        double white_R = 255, white_G = 255, white_B = 255;

        if (noiseVal < 0.7) {
            return Color.BLACK;
        } else if (noiseVal < 0.75) {
            return calculateIntermediateColor(noiseVal, black_R, orange_R, black_G, orange_G, black_B, orange_B);
        } else {
            return calculateIntermediateColor(noiseVal, black_R, white_R, black_G, white_G, black_B, white_B);
        }
    }

    private Color calculateIntermediateColor(double noiseVal, double r1, double r2, double g1, double g2, double b1, double b2) {
        double new_R = ((1 - noiseVal) * r1 + noiseVal * r2);
        double new_G = ((1 - noiseVal) * g1 + noiseVal * g2);
        double new_B = ((1 - noiseVal) * b1 + noiseVal * b2);
        new_R = Math.max(0, Math.min(255, new_R));
        new_G = Math.max(0, Math.min(255, new_G));
        new_B = Math.max(0, Math.min(255, new_B));
        return new Color((int) new_R, (int) new_G, (int) new_B);
    }

}