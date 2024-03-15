package org.dbs.spgb.domain.model;

import java.awt.*;

public class DefaultNoiseColorCalculator implements NoiseColorCalculator {
    private final double seuilBackground;
    private final double seuilMidcolor;
    private final Color backgroundColor;
    private final Color mediumColor;
    private final Color hightColor;

    public DefaultNoiseColorCalculator(Color backgroundColor, Color mediumColor, Color hightColor, double seuilBackground, double seuilMidcolor) {
        this.seuilBackground = seuilBackground;
        this.seuilMidcolor = seuilMidcolor;
        this.backgroundColor = backgroundColor;
        this.mediumColor = mediumColor;
        this.hightColor = hightColor;
    }

    @Override
    public Color calculateNoiseColor(double noiseVal) {
        if (noiseVal < seuilBackground) {
            return backgroundColor;
        } else if (noiseVal < seuilMidcolor) {
            return calculateIntermediateColor(noiseVal, backgroundColor, mediumColor);
        } else {
            return calculateIntermediateColor(noiseVal, mediumColor, hightColor);
        }

    }

    @Override
    public Color getBackGroundColor() {
        return backgroundColor;
    }

    private Color calculateIntermediateColor(double noiseVal, Color lowerBound, Color upperBound) {
        int newRed = calculateIntermediateColorComponent(noiseVal, lowerBound.getRed(), upperBound.getRed());
        int newGreen = calculateIntermediateColorComponent(noiseVal, lowerBound.getGreen(), upperBound.getGreen());
        int newBlue = calculateIntermediateColorComponent(noiseVal, lowerBound.getBlue(), upperBound.getBlue());

        return new Color(newRed, newGreen, newBlue);
    }

    private int calculateIntermediateColorComponent(double noiseVal, int lowerBoundComponent, int upperBoundComponent) {
        double newValue = ((1 - noiseVal) * lowerBoundComponent + noiseVal * upperBoundComponent);
        return Math.max(0, Math.min(255, (int) newValue));
    }

}
