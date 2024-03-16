package org.dbs.spgb.domain.service;

import org.dbs.spgb.common.UseCase;
import org.dbs.spgb.domain.model.DefaultNoiseColorCalculator;
import org.dbs.spgb.domain.model.SpaceBackGround;
import org.dbs.spgb.port.in.BuildNoiseImageUseCase;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@UseCase
public class ImagesService implements BuildNoiseImageUseCase {

    @Override
    public byte[] buildNoiseImage(ImageRequestCmd imageRequestCmd) throws IOException {
        DefaultNoiseColorCalculator noiseColorCalculator = createDefaultNoiseColorCalculator(imageRequestCmd.getColorCmd());
        SpaceBackGround spaceBackGround = new SpaceBackGround.Builder()
                .withHeight(imageRequestCmd.getSizeCmd().getHeight())
                .withWidth(imageRequestCmd.getSizeCmd().getWidth())
                .withNoiseColorCalculator(noiseColorCalculator)
                .build();
        BufferedImage image = spaceBackGround.create(imageRequestCmd.getSizeCmd().getSeed());
        return convertImageToByteArray(image);
    }

    private DefaultNoiseColorCalculator createDefaultNoiseColorCalculator(ImageRequestCmd.ColorCmd colorCmd) {
        return new DefaultNoiseColorCalculator(
                colorCmd.getBack(),
                colorCmd.getMiddle(),
                colorCmd.getFront(),
                colorCmd.getBackTreshold(),
                colorCmd.getMiddleTreshold());
    }

    private byte[] convertImageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return outputStream.toByteArray();
    }
}
