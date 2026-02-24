package org.dbs.sbgb.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImageSerializerTest {

    private ImageSerializer serializer;

    @BeforeEach
    void setUp() {
        serializer = new ImageSerializer();
    }

    @Test
    void shouldSerializeImageToPng() throws IOException {
        // Given
        BufferedImage image = createTestImage(100, 100, Color.BLUE);

        // When
        byte[] result = serializer.toByteArray(image);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSizeGreaterThan(100); // PNG has headers
    }

    @Test
    void shouldSerializeImageWithExplicitPngFormat() throws IOException {
        // Given
        BufferedImage image = createTestImage(50, 50, Color.RED);

        // When
        byte[] explicitPng = serializer.toByteArray(image, "png");
        byte[] defaultPng = serializer.toByteArray(image);

        // Then
        assertThat(explicitPng).isNotEmpty();
        assertThat(defaultPng).isNotEmpty();
        // Both should produce PNG format
        assertThat(explicitPng).hasSizeGreaterThan(100);
        assertThat(defaultPng).hasSizeGreaterThan(100);
    }

    @Test
    void shouldThrowExceptionWhenImageIsNull() {
        // When/Then
        assertThatThrownBy(() -> serializer.toByteArray(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Image cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenFormatIsNull() {
        // Given
        BufferedImage image = createTestImage(10, 10, Color.WHITE);

        // When/Then
        assertThatThrownBy(() -> serializer.toByteArray(image, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Format cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionWhenFormatIsBlank() {
        // Given
        BufferedImage image = createTestImage(10, 10, Color.WHITE);

        // When/Then
        assertThatThrownBy(() -> serializer.toByteArray(image, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Format cannot be null or blank");
    }

    @Test
    void shouldProduceDifferentSizesForDifferentImageSizes() throws IOException {
        // Given
        BufferedImage smallImage = createTestImage(10, 10, Color.GREEN);
        BufferedImage largeImage = createTestImage(100, 100, Color.GREEN);

        // When
        byte[] smallBytes = serializer.toByteArray(smallImage);
        byte[] largeBytes = serializer.toByteArray(largeImage);

        // Then
        assertThat(largeBytes.length).isGreaterThan(smallBytes.length);
    }

    private BufferedImage createTestImage(int width, int height, Color color) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return image;
    }
}
