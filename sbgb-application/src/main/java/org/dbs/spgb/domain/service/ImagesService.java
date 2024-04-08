package org.dbs.spgb.domain.service;

import org.dbs.spgb.common.UseCase;
import org.dbs.spgb.domain.model.DefaultNoiseColorCalculator;
import org.dbs.spgb.domain.model.NoiseImage;
import org.dbs.spgb.domain.model.NoiseImageCalculator;
import org.dbs.spgb.port.in.BuildNoiseImageUseCase;
import org.dbs.spgb.port.in.CreateNoiseImageUseCase;
import org.dbs.spgb.port.in.ImageRequestCmd;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@UseCase
public class ImagesService implements BuildNoiseImageUseCase, CreateNoiseImageUseCase {

    @Override
    public byte[] buildNoiseImage(ImageRequestCmd imageRequestCmd) throws IOException {

        DefaultNoiseColorCalculator noiseColorCalculator = createDefaultNoiseColorCalculator(imageRequestCmd.getColorCmd());
        NoiseImageCalculator noiseImageCalculator = new NoiseImageCalculator.Builder()
                .withHeight(imageRequestCmd.getSizeCmd().getHeight())
                .withWidth(imageRequestCmd.getSizeCmd().getWidth())
                .withNoiseColorCalculator(noiseColorCalculator)
                .build();
        BufferedImage image = noiseImageCalculator.create(imageRequestCmd.getSizeCmd().getSeed());
        return convertImageToByteArray(image);
    }

    private DefaultNoiseColorCalculator createDefaultNoiseColorCalculator(ImageRequestCmd.ColorCmd colorCmd) {
        return new DefaultNoiseColorCalculator(
                Color.decode(colorCmd.getBack()),
                Color.decode(colorCmd.getMiddle()),
                Color.decode(colorCmd.getFore()),
                colorCmd.getBackThreshold(),
                colorCmd.getMiddleThreshold());
    }

    private byte[] convertImageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return outputStream.toByteArray();
    }

    @Override
    public NoiseImage createNoiseImage(ImageRequestCmd imageRequestCmd) throws IOException {
        return null;
    }
}
