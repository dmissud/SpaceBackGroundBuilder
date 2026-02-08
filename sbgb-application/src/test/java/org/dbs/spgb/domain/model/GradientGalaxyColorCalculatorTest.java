package org.dbs.spgb.domain.model;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GradientGalaxyColorCalculatorTest {

    @Test
    void shouldThrowExceptionWhenLessThanTwoColorStops() {
        // Given
        ColorStop singleStop = new ColorStop(0.0, Color.BLACK);

        // When / Then
        assertThatThrownBy(() -> new GradientGalaxyColorCalculator(singleStop))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("At least 2 color stops required");
    }

    @Test
    void shouldReturnFirstColorForZeroIntensity() {
        // Given
        GradientGalaxyColorCalculator calculator = new GradientGalaxyColorCalculator(
            new ColorStop(0.0, Color.BLACK),
            new ColorStop(1.0, Color.WHITE)
        );

        // When
        Color result = calculator.calculateGalaxyColor(0.0);

        // Then
        assertThat(result).isEqualTo(Color.BLACK);
    }

    @Test
    void shouldReturnLastColorForOneIntensity() {
        // Given
        GradientGalaxyColorCalculator calculator = new GradientGalaxyColorCalculator(
            new ColorStop(0.0, Color.BLACK),
            new ColorStop(1.0, Color.WHITE)
        );

        // When
        Color result = calculator.calculateGalaxyColor(1.0);

        // Then
        assertThat(result).isEqualTo(Color.WHITE);
    }

    @Test
    void shouldInterpolateBetweenTwoStops() {
        // Given
        GradientGalaxyColorCalculator calculator = new GradientGalaxyColorCalculator(
            new ColorStop(0.0, 0, 0, 0),       // Black
            new ColorStop(1.0, 100, 100, 100)  // Gray
        );

        // When
        Color result = calculator.calculateGalaxyColor(0.5);

        // Then - should be approximately middle gray (with smoothstep applied)
        assertThat(result.getRed()).isBetween(40, 60);
        assertThat(result.getGreen()).isBetween(40, 60);
        assertThat(result.getBlue()).isBetween(40, 60);
    }

    @Test
    void shouldHandleMultipleColorStops() {
        // Given
        GradientGalaxyColorCalculator calculator = new GradientGalaxyColorCalculator(
            new ColorStop(0.0, 0, 0, 0),         // Black
            new ColorStop(0.5, 255, 0, 0),       // Red
            new ColorStop(1.0, 255, 255, 255)    // White
        );

        // When
        Color at25 = calculator.calculateGalaxyColor(0.25);
        Color at50 = calculator.calculateGalaxyColor(0.5);
        Color at75 = calculator.calculateGalaxyColor(0.75);

        // Then
        // At 0.25: between black and red
        assertThat(at25.getRed()).isGreaterThan(0);
        assertThat(at25.getGreen()).isEqualTo(0);
        assertThat(at25.getBlue()).isEqualTo(0);

        // At 0.5: should be red
        assertThat(at50).isEqualTo(new Color(255, 0, 0));

        // At 0.75: between red and white
        assertThat(at75.getRed()).isEqualTo(255);
        assertThat(at75.getGreen()).isGreaterThan(0);
        assertThat(at75.getBlue()).isGreaterThan(0);
    }

    @Test
    void shouldClampIntensityBelowZero() {
        // Given
        GradientGalaxyColorCalculator calculator = new GradientGalaxyColorCalculator(
            new ColorStop(0.0, Color.BLACK),
            new ColorStop(1.0, Color.WHITE)
        );

        // When
        Color result = calculator.calculateGalaxyColor(-0.5);

        // Then
        assertThat(result).isEqualTo(Color.BLACK);
    }

    @Test
    void shouldClampIntensityAboveOne() {
        // Given
        GradientGalaxyColorCalculator calculator = new GradientGalaxyColorCalculator(
            new ColorStop(0.0, Color.BLACK),
            new ColorStop(1.0, Color.WHITE)
        );

        // When
        Color result = calculator.calculateGalaxyColor(1.5);

        // Then
        assertThat(result).isEqualTo(Color.WHITE);
    }

    @Test
    void shouldReturnSpaceBackgroundColorAsFirstStop() {
        // Given
        Color expectedBackground = new Color(5, 5, 15);
        GradientGalaxyColorCalculator calculator = new GradientGalaxyColorCalculator(
            new ColorStop(0.0, expectedBackground),
            new ColorStop(1.0, Color.WHITE)
        );

        // When
        Color result = calculator.getSpaceBackgroundColor();

        // Then
        assertThat(result).isEqualTo(expectedBackground);
    }

    @Test
    void shouldCreateNebulaCalculatorFromPalette() {
        // Given
        GradientGalaxyColorCalculator calculator = ColorPalette.NEBULA.createCalculator();

        // When
        Color lowIntensity = calculator.calculateGalaxyColor(0.1);
        Color midIntensity = calculator.calculateGalaxyColor(0.5);
        Color highIntensity = calculator.calculateGalaxyColor(0.9);

        // Then - colors should be defined (not null) and different
        assertThat(lowIntensity).isNotNull();
        assertThat(midIntensity).isNotNull();
        assertThat(highIntensity).isNotNull();
        assertThat(lowIntensity).isNotEqualTo(midIntensity);
        assertThat(midIntensity).isNotEqualTo(highIntensity);
    }

    @Test
    void shouldSortColorStopsByPosition() {
        // Given - stops provided in wrong order
        GradientGalaxyColorCalculator calculator = new GradientGalaxyColorCalculator(
            new ColorStop(1.0, Color.WHITE),
            new ColorStop(0.0, Color.BLACK),
            new ColorStop(0.5, Color.RED)
        );

        // When
        Color at0 = calculator.calculateGalaxyColor(0.0);
        Color at1 = calculator.calculateGalaxyColor(1.0);

        // Then - should still work correctly
        assertThat(at0).isEqualTo(Color.BLACK);
        assertThat(at1).isEqualTo(Color.WHITE);
    }
}
