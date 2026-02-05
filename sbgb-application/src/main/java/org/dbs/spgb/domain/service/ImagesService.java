package org.dbs.spgb.domain.service;

import lombok.RequiredArgsConstructor;
import org.dbs.spgb.common.UseCase;
import org.dbs.spgb.domain.exception.ImageNameAlreadyExistsException;
import org.dbs.spgb.domain.model.*;
import org.dbs.spgb.port.in.BuildNoiseImageUseCase;
import org.dbs.spgb.port.in.CreateNoiseImageUseCase;
import org.dbs.spgb.port.in.FindNoiseImagesUseCase;
import org.dbs.spgb.port.in.ImageRequestCmd;
import org.dbs.spgb.port.out.NoiseImageRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class ImagesService implements BuildNoiseImageUseCase, CreateNoiseImageUseCase, FindNoiseImagesUseCase {

    private final NoiseImageRepository noiseImageRepository;

    @Override
    public List<NoiseImage> findAll() {
        return noiseImageRepository.findAll();
    }

    @Override
    public byte[] buildNoiseImage(ImageRequestCmd imageRequestCmd) throws IOException {

        DefaultNoiseColorCalculator noiseColorCalculator = createDefaultNoiseColorCalculator(imageRequestCmd.getColorCmd());

        BufferedImage image;
        if (imageRequestCmd.getSizeCmd().isUseMultiLayer()) {
            // Use multi-layer calculator
            MultiLayerNoiseImageCalculator.Builder multiLayerBuilder = new MultiLayerNoiseImageCalculator.Builder()
                    .withHeight(imageRequestCmd.getSizeCmd().getHeight())
                    .withWidth(imageRequestCmd.getSizeCmd().getWidth())
                    .withNoiseColorCalculator(noiseColorCalculator);

            // If custom layers are provided, use them; otherwise use preset
            if (imageRequestCmd.getSizeCmd().getLayers() != null && !imageRequestCmd.getSizeCmd().getLayers().isEmpty()) {
                List<LayerConfig> customLayers = imageRequestCmd.getSizeCmd().getLayers().stream()
                        .map(layerCmd -> LayerConfig.builder()
                                .name(layerCmd.getName())
                                .enabled(layerCmd.isEnabled())
                                .octaves(layerCmd.getOctaves())
                                .persistence(layerCmd.getPersistence())
                                .lacunarity(layerCmd.getLacunarity())
                                .scale(layerCmd.getScale())
                                .opacity(layerCmd.getOpacity())
                                .blendMode(BlendMode.valueOf(layerCmd.getBlendMode()))
                                .noiseType(NoiseType.valueOf(layerCmd.getNoiseType()))
                                .seedOffset(layerCmd.getSeedOffset())
                                .build())
                        .toList();
                multiLayerBuilder.withLayers(customLayers);
            } else {
                ImagePreset preset = ImagePreset.valueOf(imageRequestCmd.getSizeCmd().getPreset());
                multiLayerBuilder.withPreset(preset);
            }

            image = multiLayerBuilder.build().create(imageRequestCmd.getSizeCmd().getSeed());
        } else {
            // Use single-layer calculator
            NoiseImageCalculator noiseImageCalculator = new NoiseImageCalculator.Builder()
                    .withHeight(imageRequestCmd.getSizeCmd().getHeight())
                    .withWidth(imageRequestCmd.getSizeCmd().getWidth())
                    .withOctaves(imageRequestCmd.getSizeCmd().getOctaves())
                    .withPersistence(imageRequestCmd.getSizeCmd().getPersistence())
                    .withLacunarity(imageRequestCmd.getSizeCmd().getLacunarity())
                    .withScale(imageRequestCmd.getSizeCmd().getScale())
                    .withNoiseType(NoiseType.valueOf(imageRequestCmd.getSizeCmd().getNoiseType()))
                    .withNoiseColorCalculator(noiseColorCalculator)
                    .build();
            image = noiseImageCalculator.create(imageRequestCmd.getSizeCmd().getSeed());
        }

        return convertImageToByteArray(image);
    }

    private DefaultNoiseColorCalculator createDefaultNoiseColorCalculator(ImageRequestCmd.ColorCmd colorCmd) {
        InterpolationType interpolationType = InterpolationType.valueOf(colorCmd.getInterpolationType());
        return new DefaultNoiseColorCalculator(
                Color.decode(colorCmd.getBack()),
                Color.decode(colorCmd.getMiddle()),
                Color.decode(colorCmd.getFore()),
                colorCmd.getBackThreshold(),
                colorCmd.getMiddleThreshold(),
                interpolationType);
    }

    private byte[] convertImageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return outputStream.toByteArray();
    }

    @Override
    public NoiseImage createNoiseImage(ImageRequestCmd imageRequestCmd) throws IOException {
        // Validation du nom obligatoire
        if (imageRequestCmd.getName() == null || imageRequestCmd.getName().isBlank()) {
            throw new IllegalArgumentException("Le nom de l'image est obligatoire");
        }

        java.util.Optional<NoiseImage> existingImage = noiseImageRepository.findByName(imageRequestCmd.getName());

        if (existingImage.isPresent() && !imageRequestCmd.isForceUpdate()) {
            throw new ImageNameAlreadyExistsException(imageRequestCmd.getName());
        }

        byte[] imageBytes = buildNoiseImage(imageRequestCmd);

        ImageStructure structure = new ImageStructure(
                imageRequestCmd.getSizeCmd().getHeight(),
                imageRequestCmd.getSizeCmd().getWidth(),
                imageRequestCmd.getSizeCmd().getSeed(),
                imageRequestCmd.getSizeCmd().getOctaves(),
                imageRequestCmd.getSizeCmd().getPersistence(),
                imageRequestCmd.getSizeCmd().getLacunarity(),
                imageRequestCmd.getSizeCmd().getScale(),
                imageRequestCmd.getSizeCmd().getPreset(),
                imageRequestCmd.getSizeCmd().isUseMultiLayer(),
                NoiseType.valueOf(imageRequestCmd.getSizeCmd().getNoiseType()));

        ImageColor color = new ImageColor(
                imageRequestCmd.getColorCmd().getBack(),
                imageRequestCmd.getColorCmd().getMiddle(),
                imageRequestCmd.getColorCmd().getFore(),
                imageRequestCmd.getColorCmd().getBackThreshold(),
                imageRequestCmd.getColorCmd().getMiddleThreshold(),
                imageRequestCmd.getColorCmd().getInterpolationType());

        UUID id = existingImage.map(NoiseImage::getId).orElse(UUID.randomUUID());

        // Si la description est vide ou null, dupliquer le nom
        String description = (imageRequestCmd.getDescription() == null || imageRequestCmd.getDescription().isBlank())
                ? imageRequestCmd.getName()
                : imageRequestCmd.getDescription();

        NoiseImage noiseImage = new NoiseImage(
                id,
                imageRequestCmd.getName(),
                description,
                0,
                structure,
                color,
                imageBytes
        );

        return noiseImageRepository.save(noiseImage);
    }
}
