package org.dbs.sbgb.domain.service;

import lombok.RequiredArgsConstructor;
import org.dbs.sbgb.common.UseCase;
import org.dbs.sbgb.domain.model.*;
import org.dbs.sbgb.port.in.*;
import org.dbs.sbgb.port.out.NoiseBaseStructureRepository;
import org.dbs.sbgb.port.out.NoiseCosmeticRenderRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class ImagesService implements BuildNoiseImageUseCase, RateNoiseCosmeticRenderUseCase,
        FindNoiseBaseStructuresUseCase, DeleteNoiseCosmeticRenderUseCase, FindNoiseCosmeticRendersUseCase {

    private final NoiseBaseStructureRepository baseStructureRepository;
    private final NoiseCosmeticRenderRepository cosmeticRenderRepository;

    @Override
    public byte[] buildNoiseImage(ImageRequestCmd cmd) throws IOException {
        BufferedImage image = generateImage(cmd);
        return toByteArray(image);
    }

    @Override
    public NoiseCosmeticRender rate(ImageRequestCmd cmd) throws IOException {
        validateNote(cmd.getNote());

        byte[] thumbnail = buildThumbnail(cmd);
        NoiseBaseStructure base = findOrCreateBase(cmd.getSizeCmd());
        NoiseCosmeticRender render = findOrCreateRender(cmd, base, thumbnail);
        NoiseCosmeticRender savedRender = cosmeticRenderRepository.save(render);

        recalculateMaxNote(base.id());
        return savedRender;
    }

    @Override
    public List<NoiseBaseStructure> findAllSortedByMaxNoteDesc() {
        return baseStructureRepository.findAll().stream()
                .sorted(Comparator.comparingInt(NoiseBaseStructure::maxNote).reversed())
                .toList();
    }

    @Override
    public List<NoiseCosmeticRender> findRendersByBaseId(UUID baseId) {
        return cosmeticRenderRepository.findAllByBaseStructureId(baseId);
    }

    @Override
    public void deleteRender(UUID renderId) {
        NoiseCosmeticRender render = cosmeticRenderRepository.findById(renderId)
                .orElseThrow(() -> new IllegalArgumentException("Render not found: " + renderId));
        UUID baseId = render.baseStructureId();

        cosmeticRenderRepository.deleteById(renderId);

        List<NoiseCosmeticRender> remaining = cosmeticRenderRepository.findAllByBaseStructureId(baseId);
        if (remaining.isEmpty()) {
            baseStructureRepository.deleteById(baseId);
        } else {
            int maxNote = remaining.stream().mapToInt(NoiseCosmeticRender::note).max().orElse(0);
            baseStructureRepository.updateMaxNote(baseId, maxNote);
        }
    }

    private void validateNote(int note) {
        if (note < 1 || note > 5) {
            throw new IllegalArgumentException("Note must be between 1 and 5, got: " + note);
        }
    }

    private byte[] buildThumbnail(ImageRequestCmd cmd) throws IOException {
        ImageRequestCmd.SizeCmd originalSize = cmd.getSizeCmd();
        ImageRequestCmd thumbnailCmd = ImageRequestCmd.builder()
                .sizeCmd(ImageRequestCmd.SizeCmd.builder()
                        .width(200).height(200)
                        .seed(originalSize.getSeed())
                        .octaves(originalSize.getOctaves())
                        .persistence(originalSize.getPersistence())
                        .lacunarity(originalSize.getLacunarity())
                        .scale(originalSize.getScale())
                        .preset(originalSize.getPreset())
                        .useMultiLayer(originalSize.isUseMultiLayer())
                        .noiseType(originalSize.getNoiseType())
                        .layers(originalSize.getLayers())
                        .build())
                .colorCmd(cmd.getColorCmd())
                .build();
        return buildNoiseImage(thumbnailCmd);
    }

    private NoiseBaseStructure findOrCreateBase(ImageRequestCmd.SizeCmd sizeCmd) {
        int configHash = computeConfigHash(sizeCmd);
        return baseStructureRepository.findByConfigHash(configHash)
                .orElseGet(() -> {
                    NoiseBaseStructure newBase = buildBaseStructure(sizeCmd, configHash);
                    return baseStructureRepository.save(newBase);
                });
    }

    private NoiseCosmeticRender findOrCreateRender(ImageRequestCmd cmd, NoiseBaseStructure base, byte[] thumbnail) {
        int cosmeticHash = computeCosmeticHash(cmd.getColorCmd());
        Optional<NoiseCosmeticRender> existing = cosmeticRenderRepository
                .findByBaseStructureIdAndCosmeticHash(base.id(), cosmeticHash);

        return existing.isPresent()
                ? updateWithNewNote(existing.get(), cmd.getNote(), thumbnail)
                : createNewRender(cmd, base, thumbnail);
    }

    private NoiseCosmeticRender updateWithNewNote(NoiseCosmeticRender existing, int note, byte[] thumbnail) {
        return new NoiseCosmeticRender(existing.id(), existing.baseStructureId(), existing.back(), existing.middle(),
                existing.fore(), existing.backThreshold(), existing.middleThreshold(), existing.interpolationType(),
                existing.transparentBackground(), note, thumbnail, existing.description());
    }

    private NoiseCosmeticRender createNewRender(ImageRequestCmd cmd, NoiseBaseStructure base, byte[] thumbnail) {
        String description = buildCosmeticDescription(cmd.getColorCmd());
        ImageRequestCmd.ColorCmd color = cmd.getColorCmd();
        return new NoiseCosmeticRender(UUID.randomUUID(), base.id(),
                color.getBack(), color.getMiddle(), color.getFore(),
                color.getBackThreshold(), color.getMiddleThreshold(),
                color.getInterpolationType(), color.isTransparentBackground(),
                cmd.getNote(), thumbnail, description);
    }

    private void recalculateMaxNote(UUID baseId) {
        int maxNote = cosmeticRenderRepository.findAllByBaseStructureId(baseId).stream()
                .mapToInt(NoiseCosmeticRender::note).max().orElse(0);
        baseStructureRepository.updateMaxNote(baseId, maxNote);
    }

    private int computeConfigHash(ImageRequestCmd.SizeCmd sizeCmd) {
        String layersConfig = layersToString(sizeCmd);
        return new NoiseBaseStructure(null, null, 0, sizeCmd.getWidth(), sizeCmd.getHeight(), sizeCmd.getSeed(),
                sizeCmd.getOctaves(), sizeCmd.getPersistence(), sizeCmd.getLacunarity(), sizeCmd.getScale(),
                sizeCmd.getNoiseType(), sizeCmd.isUseMultiLayer(), layersConfig).configHash();
    }

    private int computeCosmeticHash(ImageRequestCmd.ColorCmd colorCmd) {
        return new NoiseCosmeticRender(null, null, colorCmd.getBack(), colorCmd.getMiddle(), colorCmd.getFore(),
                colorCmd.getBackThreshold(), colorCmd.getMiddleThreshold(), colorCmd.getInterpolationType(),
                colorCmd.isTransparentBackground(), 0, null, null).cosmeticHash();
    }

    private NoiseBaseStructure buildBaseStructure(ImageRequestCmd.SizeCmd sizeCmd, int configHash) {
        String layersConfig = layersToString(sizeCmd);
        NoiseBaseStructure template = new NoiseBaseStructure(null, null, 0, sizeCmd.getWidth(), sizeCmd.getHeight(),
                sizeCmd.getSeed(), sizeCmd.getOctaves(), sizeCmd.getPersistence(), sizeCmd.getLacunarity(),
                sizeCmd.getScale(), sizeCmd.getNoiseType(), sizeCmd.isUseMultiLayer(), layersConfig);
        return new NoiseBaseStructure(UUID.randomUUID(), template.generateDescription(), 0,
                sizeCmd.getWidth(), sizeCmd.getHeight(), sizeCmd.getSeed(), sizeCmd.getOctaves(),
                sizeCmd.getPersistence(), sizeCmd.getLacunarity(), sizeCmd.getScale(),
                sizeCmd.getNoiseType(), sizeCmd.isUseMultiLayer(), layersConfig);
    }

    private String buildCosmeticDescription(ImageRequestCmd.ColorCmd colorCmd) {
        NoiseCosmeticRender template = new NoiseCosmeticRender(null, null,
                colorCmd.getBack(), colorCmd.getMiddle(), colorCmd.getFore(),
                colorCmd.getBackThreshold(), colorCmd.getMiddleThreshold(), colorCmd.getInterpolationType(),
                colorCmd.isTransparentBackground(), 0, null, null);
        return template.generateDescription();
    }

    private String layersToString(ImageRequestCmd.SizeCmd sizeCmd) {
        if (!sizeCmd.isUseMultiLayer() || sizeCmd.getLayers() == null) {
            return null;
        }
        return sizeCmd.getLayers().toString();
    }

    private BufferedImage generateImage(ImageRequestCmd cmd) {
        DefaultNoiseColorCalculator colorCalculator = createColorCalculator(cmd.getColorCmd());
        if (cmd.getSizeCmd().isUseMultiLayer()) {
            return buildMultiLayerImage(cmd.getSizeCmd(), colorCalculator);
        }
        return buildSingleLayerImage(cmd.getSizeCmd(), colorCalculator);
    }

    private BufferedImage buildSingleLayerImage(ImageRequestCmd.SizeCmd sizeCmd, DefaultNoiseColorCalculator colorCalculator) {
        return new NoiseImageCalculator.Builder()
                .withHeight(sizeCmd.getHeight())
                .withWidth(sizeCmd.getWidth())
                .withOctaves(sizeCmd.getOctaves())
                .withPersistence(sizeCmd.getPersistence())
                .withLacunarity(sizeCmd.getLacunarity())
                .withScale(sizeCmd.getScale())
                .withNoiseType(NoiseType.valueOf(sizeCmd.getNoiseType()))
                .withNoiseColorCalculator(colorCalculator)
                .build()
                .create(sizeCmd.getSeed());
    }

    private BufferedImage buildMultiLayerImage(ImageRequestCmd.SizeCmd sizeCmd, DefaultNoiseColorCalculator colorCalculator) {
        MultiLayerNoiseImageCalculator.Builder builder = new MultiLayerNoiseImageCalculator.Builder()
                .withHeight(sizeCmd.getHeight())
                .withWidth(sizeCmd.getWidth())
                .withNoiseColorCalculator(colorCalculator);

        if (sizeCmd.getLayers() != null && !sizeCmd.getLayers().isEmpty()) {
            List<LayerConfig> customLayers = sizeCmd.getLayers().stream()
                    .map(this::toLayerConfig)
                    .toList();
            builder.withLayers(customLayers);
        } else {
            builder.withPreset(ImagePreset.valueOf(sizeCmd.getPreset()));
        }

        return builder.build().create(sizeCmd.getSeed());
    }

    private DefaultNoiseColorCalculator createColorCalculator(ImageRequestCmd.ColorCmd colorCmd) {
        return new DefaultNoiseColorCalculator(
                Color.decode(colorCmd.getBack()),
                Color.decode(colorCmd.getMiddle()),
                Color.decode(colorCmd.getFore()),
                colorCmd.getBackThreshold(),
                colorCmd.getMiddleThreshold(),
                InterpolationType.valueOf(colorCmd.getInterpolationType()),
                colorCmd.isTransparentBackground());
    }

    private LayerConfig toLayerConfig(ImageRequestCmd.LayerCmd l) {
        return LayerConfig.builder()
                .name(l.getName())
                .enabled(l.isEnabled())
                .octaves(l.getOctaves())
                .persistence(l.getPersistence())
                .lacunarity(l.getLacunarity())
                .scale(l.getScale())
                .opacity(l.getOpacity())
                .blendMode(BlendMode.valueOf(l.getBlendMode()))
                .noiseType(NoiseType.valueOf(l.getNoiseType()))
                .seedOffset(l.getSeedOffset())
                .build();
    }

    private byte[] toByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return outputStream.toByteArray();
    }
}
