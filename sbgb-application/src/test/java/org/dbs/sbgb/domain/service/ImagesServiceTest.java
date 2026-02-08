package org.dbs.sbgb.domain.service;

import org.dbs.sbgb.domain.exception.ImageNameAlreadyExistsException;
import org.dbs.sbgb.domain.model.NoiseImage;
import org.dbs.sbgb.port.in.ImageRequestCmd;
import org.dbs.sbgb.port.out.NoiseImageRepository;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ImagesServiceTest {

    @Test
    void createNoiseImageUniquenessTest() throws IOException {
        final List<NoiseImage> db = new ArrayList<>();
        NoiseImageRepository repository = new NoiseImageRepository() {
            @Override
            public NoiseImage save(NoiseImage noiseImage) {
                db.removeIf(i -> i.getName().equals(noiseImage.getName()));
                db.add(noiseImage);
                return noiseImage;
            }

            @Override
            public List<NoiseImage> findAll() {
                return db;
            }

            @Override
            public java.util.Optional<NoiseImage> findByName(String name) {
                return db.stream().filter(i -> i.getName().equals(name)).findFirst();
            }
        };

        ImagesService imagesService = new ImagesService(repository);
        ImageRequestCmd cmd = createSimpleCmd("TestImage", false);

        // First creation
        imagesService.createNoiseImage(cmd);
        assertEquals(1, db.size());

        // Second creation with same name - should fail
        assertThrows(ImageNameAlreadyExistsException.class, () -> imagesService.createNoiseImage(cmd));
        assertEquals(1, db.size());
    }

    @Test
    void updateNoiseImageTest() throws IOException {
        final List<NoiseImage> db = new ArrayList<>();
        NoiseImageRepository repository = new NoiseImageRepository() {
            @Override
            public NoiseImage save(NoiseImage noiseImage) {
                db.removeIf(i -> i.getName().equals(noiseImage.getName()));
                db.add(noiseImage);
                return noiseImage;
            }

            @Override
            public List<NoiseImage> findAll() {
                return db;
            }

            @Override
            public java.util.Optional<NoiseImage> findByName(String name) {
                return db.stream().filter(i -> i.getName().equals(name)).findFirst();
            }
        };

        ImagesService imagesService = new ImagesService(repository);
        ImageRequestCmd cmd1 = createSimpleCmd("UpdateMe", false);

        // Initial creation
        NoiseImage saved1 = imagesService.createNoiseImage(cmd1);
        UUID firstId = saved1.getId();

        // Update with forceUpdate = true
        ImageRequestCmd cmdUpdate = createSimpleCmd("UpdateMe", true);
        NoiseImage updated = imagesService.createNoiseImage(cmdUpdate);

        assertEquals(1, db.size());
        assertEquals(firstId, updated.getId(), "UUID should remain the same on update");
    }

    private ImageRequestCmd createSimpleCmd(String name, boolean forceUpdate) {
        return ImageRequestCmd.builder()
                .name(name)
                .forceUpdate(forceUpdate)
                .sizeCmd(ImageRequestCmd.SizeCmd.builder()
                        .height(100).width(100).seed(123).build())
                .colorCmd(ImageRequestCmd.ColorCmd.builder()
                        .back("#000000").middle("#111111").fore("#222222")
                        .backThreshold(0.5).middleThreshold(0.6).build())
                .build();
    }

    @Test
    void buildNoiseImageTest() throws IOException {
        NoiseImageRepository repository = new NoiseImageRepository() {
            @Override
            public NoiseImage save(NoiseImage noiseImage) {
                return noiseImage;
            }

            @Override
            public List<NoiseImage> findAll() {
                return Collections.emptyList();
            }

            @Override
            public java.util.Optional<NoiseImage> findByName(String name) {
                return java.util.Optional.empty();
            }
        };
        ImagesService imagesService = new ImagesService(repository);
        ImageRequestCmd.SizeCmd sizeCmd = ImageRequestCmd.SizeCmd.builder()
                .height(500)
                .width(500)
                .seed(2659)
                .octaves(3)
                .persistence(0.5)
                .lacunarity(2.0)
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

    @Test
    void buildRidgedNoiseImageTest() throws IOException {
        NoiseImageRepository repository = new NoiseImageRepository() {
            @Override
            public NoiseImage save(NoiseImage noiseImage) {
                return noiseImage;
            }

            @Override
            public List<NoiseImage> findAll() {
                return Collections.emptyList();
            }

            @Override
            public java.util.Optional<NoiseImage> findByName(String name) {
                return java.util.Optional.empty();
            }
        };
        ImagesService imagesService = new ImagesService(repository);
        ImageRequestCmd.SizeCmd sizeCmd = ImageRequestCmd.SizeCmd.builder()
                .height(200)
                .width(200)
                .seed(1234)
                .octaves(4)
                .persistence(0.5)
                .lacunarity(2.0)
                .noiseType("RIDGED")
                .build();
        ImageRequestCmd.ColorCmd colorCmd = ImageRequestCmd.ColorCmd.builder()
                .back("#000000")
                .middle("#777777")
                .fore("#FFFFFF")
                .backThreshold(0.3)
                .middleThreshold(0.7)
                .build();
        ImageRequestCmd imageRequestCmd = ImageRequestCmd.builder()
                .sizeCmd(sizeCmd)
                .colorCmd(colorCmd)
                .build();

        byte[] result = imagesService.buildNoiseImage(imageRequestCmd);

        assertNotNull(result);
        InputStream in = new ByteArrayInputStream(result);
        BufferedImage outImage = ImageIO.read(in);
        assertEquals(200, outImage.getHeight());
        assertEquals(200, outImage.getWidth());
    }
}