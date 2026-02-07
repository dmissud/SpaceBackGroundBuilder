package org.dbs.spgb.domain.model;

import java.awt.*;

public class DefaultNoiseColorCalculator implements NoiseColorCalculator {
    private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

    private final double seuilBackground;
    private final double seuilMidcolor;
    private final Color backgroundColor;
    private final Color mediumColor;
    private final Color hightColor;
    private final InterpolationType interpolationType;
    private final boolean transparentBackground;

    public DefaultNoiseColorCalculator(Color backgroundColor, Color mediumColor, Color hightColor, double seuilBackground, double seuilMidcolor, InterpolationType interpolationType, boolean transparentBackground) {
        this.seuilBackground = seuilBackground;
        this.seuilMidcolor = seuilMidcolor;
        this.backgroundColor = backgroundColor;
        this.mediumColor = mediumColor;
        this.hightColor = hightColor;
        this.interpolationType = interpolationType;
        this.transparentBackground = transparentBackground;
    }

    public DefaultNoiseColorCalculator(Color backgroundColor, Color mediumColor, Color hightColor, double seuilBackground, double seuilMidcolor, InterpolationType interpolationType) {
        this(backgroundColor, mediumColor, hightColor, seuilBackground, seuilMidcolor, interpolationType, false);
    }

    @Override
    public Color calculateNoiseColor(double noiseVal) {
        if (noiseVal < seuilBackground) {
            return transparentBackground ? TRANSPARENT : backgroundColor;
        } else if (noiseVal < seuilMidcolor) {
            Color effectiveBg = transparentBackground
                    ? new Color(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), 0)
                    : backgroundColor;
            return calculateIntermediateColor(noiseVal, effectiveBg, mediumColor);
        } else {
            return calculateIntermediateColor(noiseVal, mediumColor, hightColor);
        }

    }

    @Override
    public Color getBackGroundColor() {
        return transparentBackground ? TRANSPARENT : backgroundColor;
    }

    private Color calculateIntermediateColor(double noiseVal, Color lowerBound, Color upperBound) {
        int newRed = calculateIntermediateColorComponent(noiseVal, lowerBound.getRed(), upperBound.getRed());
        int newGreen = calculateIntermediateColorComponent(noiseVal, lowerBound.getGreen(), upperBound.getGreen());
        int newBlue = calculateIntermediateColorComponent(noiseVal, lowerBound.getBlue(), upperBound.getBlue());
        int newAlpha = calculateIntermediateColorComponent(noiseVal, lowerBound.getAlpha(), upperBound.getAlpha());

        return new Color(newRed, newGreen, newBlue, newAlpha);
    }

    private int calculateIntermediateColorComponent(double noiseVal, int lowerBoundComponent, int upperBoundComponent) {
        double interpolatedValue = applyInterpolation(noiseVal);
        double newValue = ((1 - interpolatedValue) * lowerBoundComponent + interpolatedValue * upperBoundComponent);
        return Math.clamp((int) newValue, 0, 255);
    }

    private double applyInterpolation(double t) {
        return switch (interpolationType) {
            case LINEAR -> t;
            case SMOOTHSTEP -> smoothstep(t);
            case SMOOTHERSTEP -> smootherstep(t);
            case COSINE -> cosineInterpolation(t);
            case POWER_2 -> Math.pow(t, 2.0);
            case POWER_3 -> Math.pow(t, 3.0);
        };
    }

    private double smoothstep(double t) {
        return t * t * (3.0 - 2.0 * t);
    }

    private double smootherstep(double t) {
        return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);
    }

    private double cosineInterpolation(double t) {
        return (1.0 - Math.cos(t * Math.PI)) / 2.0;
    }

}
