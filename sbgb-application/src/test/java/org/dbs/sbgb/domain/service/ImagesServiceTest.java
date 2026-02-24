package org.dbs.sbgb.domain.service;

import org.dbs.sbgb.domain.model.NoiseImage;
import org.dbs.sbgb.port.in.ImageRequestCmd;
import org.dbs.sbgb.port.out.NoiseImageRepository;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ImagesServiceTest {

    @Test
    void shouldCreateNoiseImageWithUniqueId() throws IOException {
        final List<NoiseImage> db = new ArrayList<>();
        NoiseImageRepository repository = stubRepository(db);

        ImagesService imagesService = new ImagesService(repository);
        NoiseImage saved1 = imagesService.createNoiseImage(createSimpleCmd(3));
        NoiseImage saved2 = imagesService.createNoiseImage(createSimpleCmd(4));

        assertThat(db).hasSize(2);
        assertThat(saved1.getId()).isNotEqualTo(saved2.getId());
    }

    @Test
    void shouldCreateNoiseImageWithNote() throws IOException {
        final List<NoiseImage> db = new ArrayList<>();
        NoiseImageRepository repository = stubRepository(db);

        ImagesService imagesService = new ImagesService(repository);
        NoiseImage saved = imagesService.createNoiseImage(createSimpleCmd(4));

        assertThat(saved.getNote()).isEqualTo(4);
    }

    @Test
    void shouldUpdateNoteForExistingImage() {
        final List<NoiseImage> db = new ArrayList<>();
        UUID id = UUID.randomUUID();
        db.add(new NoiseImage(id, "test", "desc", 1, null, null, new byte[0]));

        NoiseImageRepository repository = new NoiseImageRepository() {
            @Override public NoiseImage save(NoiseImage n) { db.add(n); return n; }
            @Override public List<NoiseImage> findAll() { return db; }
            @Override public Optional<NoiseImage> findByName(String name) { return Optional.empty(); }
            @Override public void updateNote(UUID id, int note) {
                db.stream().filter(i -> i.getId().equals(id)).findFirst()
                        .ifPresent(i -> i.setNote(note));
            }
        };

        ImagesService imagesService = new ImagesService(repository);
        imagesService.updateNote(id, 5);

        assertThat(db.get(0).getNote()).isEqualTo(5);
    }

    @Test
    void shouldBuildNoiseImage() throws IOException {
        NoiseImageRepository repository = stubRepository(new ArrayList<>());
        ImagesService imagesService = new ImagesService(repository);

        byte[] result = imagesService.buildNoiseImage(ImageRequestCmd.builder()
                .sizeCmd(ImageRequestCmd.SizeCmd.builder()
                        .height(500).width(500).seed(2659).octaves(3)
                        .persistence(0.5).lacunarity(2.0).build())
                .colorCmd(ImageRequestCmd.ColorCmd.builder()
                        .back("#01FD37").middle("#B537FD").fore("#FD7812")
                        .backThreshold(0.7).middleThreshold(0.75).build())
                .build());

        InputStream in = new ByteArrayInputStream(result);
        BufferedImage outImage = ImageIO.read(in);
        assertEquals(500, outImage.getHeight());
        assertEquals(500, outImage.getWidth());
    }

    @Test
    void shouldBuildRidgedNoiseImage() throws IOException {
        NoiseImageRepository repository = stubRepository(new ArrayList<>());
        ImagesService imagesService = new ImagesService(repository);

        byte[] result = imagesService.buildNoiseImage(ImageRequestCmd.builder()
                .sizeCmd(ImageRequestCmd.SizeCmd.builder()
                        .height(200).width(200).seed(1234).octaves(4)
                        .persistence(0.5).lacunarity(2.0).noiseType("RIDGED").build())
                .colorCmd(ImageRequestCmd.ColorCmd.builder()
                        .back("#000000").middle("#777777").fore("#FFFFFF")
                        .backThreshold(0.3).middleThreshold(0.7).build())
                .build());

        assertNotNull(result);
        BufferedImage outImage = ImageIO.read(new ByteArrayInputStream(result));
        assertEquals(200, outImage.getHeight());
        assertEquals(200, outImage.getWidth());
    }

    private NoiseImageRepository stubRepository(List<NoiseImage> db) {
        return new NoiseImageRepository() {
            @Override public NoiseImage save(NoiseImage n) { db.add(n); return n; }
            @Override public List<NoiseImage> findAll() { return db; }
            @Override public Optional<NoiseImage> findByName(String name) { return Optional.empty(); }
            @Override public void updateNote(UUID id, int note) {}
        };
    }

    private ImageRequestCmd createSimpleCmd(int note) {
        return ImageRequestCmd.builder()
                .note(note)
                .sizeCmd(ImageRequestCmd.SizeCmd.builder()
                        .height(100).width(100).seed(123).build())
                .colorCmd(ImageRequestCmd.ColorCmd.builder()
                        .back("#000000").middle("#111111").fore("#222222")
                        .backThreshold(0.5).middleThreshold(0.6).build())
                .build();
    }
}
