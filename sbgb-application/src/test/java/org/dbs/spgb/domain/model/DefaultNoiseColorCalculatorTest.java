package org.dbs.spgb.domain.model;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class DefaultNoiseColorCalculatorTest {

    private static final Color BG = new Color(0, 0, 0);       // black
    private static final Color MID = new Color(128, 128, 128); // gray
    private static final Color FORE = new Color(255, 255, 255); // white
    private static final double BACK_THRESHOLD = 0.3;
    private static final double MID_THRESHOLD = 0.7;

    @Test
    void shouldReturnOpaqueBackgroundColorWhenNotTransparent() {
        DefaultNoiseColorCalculator calc = new DefaultNoiseColorCalculator(
                BG, MID, FORE, BACK_THRESHOLD, MID_THRESHOLD, InterpolationType.LINEAR, false);

        Color result = calc.calculateNoiseColor(0.1);

        assertEquals(BG.getRed(), result.getRed());
        assertEquals(BG.getGreen(), result.getGreen());
        assertEquals(BG.getBlue(), result.getBlue());
        assertEquals(255, result.getAlpha());
    }

    @Test
    void shouldReturnTransparentColorWhenTransparentAndBelowThreshold() {
        DefaultNoiseColorCalculator calc = new DefaultNoiseColorCalculator(
                BG, MID, FORE, BACK_THRESHOLD, MID_THRESHOLD, InterpolationType.LINEAR, true);

        Color result = calc.calculateNoiseColor(0.1);

        assertEquals(0, result.getAlpha());
    }

    @Test
    void shouldReturnFullyOpaqueColorAboveMiddleThresholdWhenTransparent() {
        DefaultNoiseColorCalculator calc = new DefaultNoiseColorCalculator(
                BG, MID, FORE, BACK_THRESHOLD, MID_THRESHOLD, InterpolationType.LINEAR, true);

        Color result = calc.calculateNoiseColor(0.9);

        assertEquals(255, result.getAlpha());
    }

    @Test
    void shouldReturnPartialAlphaInTransitionZoneWhenTransparent() {
        DefaultNoiseColorCalculator calc = new DefaultNoiseColorCalculator(
                BG, MID, FORE, BACK_THRESHOLD, MID_THRESHOLD, InterpolationType.LINEAR, true);

        // Value in the back->mid transition zone
        Color result = calc.calculateNoiseColor(0.5);

        assertTrue(result.getAlpha() > 0, "Alpha should be > 0 in transition zone");
        assertTrue(result.getAlpha() < 255, "Alpha should be < 255 in transition zone");
    }

    @Test
    void shouldReturnOpaqueInTransitionZoneWhenNotTransparent() {
        DefaultNoiseColorCalculator calc = new DefaultNoiseColorCalculator(
                BG, MID, FORE, BACK_THRESHOLD, MID_THRESHOLD, InterpolationType.LINEAR, false);

        Color result = calc.calculateNoiseColor(0.5);

        assertEquals(255, result.getAlpha());
    }

    @Test
    void getBackGroundColorShouldReturnTransparentWhenFlagIsTrue() {
        DefaultNoiseColorCalculator calc = new DefaultNoiseColorCalculator(
                BG, MID, FORE, BACK_THRESHOLD, MID_THRESHOLD, InterpolationType.LINEAR, true);

        Color bgColor = calc.getBackGroundColor();

        assertEquals(0, bgColor.getAlpha());
    }

    @Test
    void getBackGroundColorShouldReturnOpaqueWhenFlagIsFalse() {
        DefaultNoiseColorCalculator calc = new DefaultNoiseColorCalculator(
                BG, MID, FORE, BACK_THRESHOLD, MID_THRESHOLD, InterpolationType.LINEAR, false);

        Color bgColor = calc.getBackGroundColor();

        assertEquals(255, bgColor.getAlpha());
        assertEquals(BG, bgColor);
    }

    @Test
    void backwardCompatibleConstructorShouldDefaultToNotTransparent() {
        DefaultNoiseColorCalculator calc = new DefaultNoiseColorCalculator(
                BG, MID, FORE, BACK_THRESHOLD, MID_THRESHOLD, InterpolationType.LINEAR);

        Color result = calc.calculateNoiseColor(0.1);

        assertEquals(255, result.getAlpha());
        assertEquals(BG, calc.getBackGroundColor());
    }

    @Test
    void midToForeTransitionShouldAlwaysBeOpaqueEvenWhenTransparent() {
        DefaultNoiseColorCalculator calc = new DefaultNoiseColorCalculator(
                BG, MID, FORE, BACK_THRESHOLD, MID_THRESHOLD, InterpolationType.LINEAR, true);

        // Value in the mid->fore zone
        Color result = calc.calculateNoiseColor(0.85);

        assertEquals(255, result.getAlpha());
    }
}
