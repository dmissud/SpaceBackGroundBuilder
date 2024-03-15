package org.dbs.spgb.domain.service;

import org.dbs.spgb.common.UseCase;
import org.dbs.spgb.domain.model.DefaultNoiseColorCalculator;
import org.dbs.spgb.domain.model.SpaceBackGround;
import org.dbs.spgb.port.in.BuildNoiseImageUseCase;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@UseCase
public class ImagesService implements BuildNoiseImageUseCase {
    private static final Color BLACK = new Color(0, 0, 0);
    private static final Color ORANGE = new Color(255, 165, 0);
    private static final Color WHITE = new Color(255, 255, 255);
    public static final double BACKGROUND_THRESHOLD = 0.7;
    public static final double MIDCOLOR_THRESHOLD = 0.75;

    @Override
    public byte[] buildNoiseImage(ImageRequestCmd imageRequestCmd) throws IOException {
        DefaultNoiseColorCalculator noiseColorCalculator = createDefaultNoiseColorCalculator();
        SpaceBackGround spaceBackGround = new SpaceBackGround.Builder()
                .withHeight(imageRequestCmd.getHeight())
                .withWidth(imageRequestCmd.getWidth())
                .withNoiseColorCalculator(noiseColorCalculator)
                .build();
        BufferedImage image = spaceBackGround.create(imageRequestCmd.getSeed());
        return convertImageToByteArray(image);
    }

    private DefaultNoiseColorCalculator createDefaultNoiseColorCalculator() {
        return new DefaultNoiseColorCalculator(BLACK, ORANGE, WHITE, BACKGROUND_THRESHOLD, MIDCOLOR_THRESHOLD);
    }

    private byte[] convertImageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return outputStream.toByteArray();
    }
}
