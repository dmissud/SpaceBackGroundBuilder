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
        ImagesService imagesService = new ImagesService(noiseImage -> noiseImage);
        ImageRequestCmd.SizeCmd sizeCmd = ImageRequestCmd.SizeCmd.builder()
                .height(500)
                .width(500)
                .seed(2659)
                .build();
        ImageRequestCmd.ColorCmd colorCmd = ImageRequestCmd.ColorCmd.builder()
                .back("#01FD37")
                .middle("#B537FD")
                .fore("#FD7812")
                .backThreshold(0.7)
                .middleThreshold(0.75)
                .build();
        ImageRequestCmd imageRequestCmd = ImageRequestCmd.builder()
                .sizeCmd(sizeCmd)
                .colorCmd(colorCmd)
                .build();

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