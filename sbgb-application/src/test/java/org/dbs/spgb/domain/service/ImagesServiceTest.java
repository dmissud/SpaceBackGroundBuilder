package org.dbs.spgb.domain.service;

import org.dbs.spgb.port.in.ImageRequestCmd;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ImagesServiceTest {

    // We are testing the "ImagesService" class, particularly its method "buildNoiseImage".
    // This method has the responsibility of taking an image request, creating a noise colored
    // image based on the request parameters, and returning it in form of a byte array.

    @Test
    void buildNoiseImageTest() throws IOException {
        ImagesService imagesService = new ImagesService();
        ImageRequestCmd.SizeCmd sizeCmd = new ImageRequestCmd.SizeCmd(500, 500, 2659);
        ImageRequestCmd.ColorCmd colorCmd = new ImageRequestCmd.ColorCmd("#01FD37", "#B537FD", "#FD7812", 0.7, 0.75);
        ImageRequestCmd imageRequestCmd = new ImageRequestCmd(sizeCmd, colorCmd);

        byte[] result = imagesService.buildNoiseImage(imageRequestCmd);

        // Let's ensure that the method returns a valid PNG image by trying to decode the image
        try {
            InputStream in = new ByteArrayInputStream(result);
            BufferedImage outImage = ImageIO.read(in);
            assertEquals(500, outImage.getHeight());
            assertEquals(500, outImage.getWidth());
        } catch (Exception e) {
            fail("Failed to decode image");
        }
    }
}