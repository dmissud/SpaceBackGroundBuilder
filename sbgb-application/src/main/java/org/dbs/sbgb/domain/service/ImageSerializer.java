package org.dbs.sbgb.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Service for serializing BufferedImage to byte arrays.
 * Encapsulates image format conversion logic.
 */
@Slf4j
@Component
public class ImageSerializer {

    private static final String DEFAULT_FORMAT = "png";

    /**
     * Serialize a BufferedImage to byte array in PNG format.
     *
     * @param image the image to serialize
     * @return byte array representation of the image
     * @throws IOException if serialization fails
     */
    public byte[] toByteArray(BufferedImage image) throws IOException {
        return toByteArray(image, DEFAULT_FORMAT);
    }

    /**
     * Serialize a BufferedImage to byte array in specified format.
     *
     * @param image the image to serialize
     * @param format the image format (e.g., "png", "jpg")
     * @return byte array representation of the image
     * @throws IOException if serialization fails
     */
    public byte[] toByteArray(BufferedImage image, String format) throws IOException {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        if (format == null || format.isBlank()) {
            throw new IllegalArgumentException("Format cannot be null or blank");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean written = ImageIO.write(image, format, baos);

        if (!written) {
            throw new IOException("No appropriate writer found for format: " + format);
        }

        log.debug("Serialized image to {} format: {} bytes", format, baos.size());
        return baos.toByteArray();
    }
}
