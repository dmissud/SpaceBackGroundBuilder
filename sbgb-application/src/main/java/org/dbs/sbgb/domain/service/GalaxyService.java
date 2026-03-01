package org.dbs.sbgb.domain.service;

import lombok.RequiredArgsConstructor;
import org.dbs.sbgb.common.UseCase;
import org.dbs.sbgb.domain.model.*;
import org.dbs.sbgb.port.in.*;
import org.dbs.sbgb.port.out.GalaxyBaseStructureRepository;
import org.dbs.sbgb.port.out.GalaxyCosmeticRenderRepository;
import org.dbs.sbgb.port.out.GalaxyImageComputationPort;
import org.springframework.cache.annotation.CacheEvict;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class GalaxyService implements BuildGalaxyImageUseCase, RateGalaxyCosmeticRenderUseCase,
        FindGalaxyBaseStructuresUseCase, FindGalaxyCosmeticRendersUseCase, DeleteGalaxyCosmeticRenderUseCase,
        DeleteRendersByBaseUseCase, ReapplyGalaxyCosmeticsUseCase {

    private final GalaxyBaseStructureRepository baseStructureRepository;
    private final GalaxyCosmeticRenderRepository cosmeticRenderRepository;
    private final ImageSerializer imageSerializer;
    private final GalaxyImageComputationPort galaxyImageComputationPort;

    @Override
    public byte[] buildGalaxyImage(GalaxyRequestCmd cmd) throws IOException {
        BufferedImage image = generateGalaxyBufferedImage(cmd);
        return imageSerializer.toByteArray(image);
    }

    @Override
    @CacheEvict(value = "galaxyImage", allEntries = true)
    public GalaxyCosmeticRender rate(GalaxyRequestCmd cmd) throws IOException {
        validateNote(cmd.getNote());

        byte[] thumbnail = buildThumbnail(cmd);
        GalaxyBaseStructure base = findOrCreateBase(cmd);
        GalaxyCosmeticRender render = findOrCreateRender(cmd, base, thumbnail);
        GalaxyCosmeticRender savedRender = cosmeticRenderRepository.save(render);

        recalculateMaxNote(base.id());
        return savedRender;
    }

    @Override
    public List<GalaxyBaseStructure> findAllSortedByMaxNoteDesc() {
        return baseStructureRepository.findAll().stream()
                .sorted(Comparator.comparingInt(GalaxyBaseStructure::maxNote).reversed())
                .toList();
    }

    @Override
    public List<GalaxyCosmeticRender> findRendersByBaseId(UUID baseId) {
        return cosmeticRenderRepository.findAllByBaseStructureId(baseId);
    }

    @Override
    public void deleteRender(UUID renderId) {
        GalaxyCosmeticRender render = cosmeticRenderRepository.findById(renderId)
                .orElseThrow(() -> new IllegalArgumentException("Render not found: " + renderId));
        UUID baseId = render.baseStructureId();

        cosmeticRenderRepository.deleteById(renderId);

        List<GalaxyCosmeticRender> remaining = cosmeticRenderRepository.findAllByBaseStructureId(baseId);
        if (remaining.isEmpty()) {
            baseStructureRepository.deleteById(baseId);
        } else {
            int maxNote = remaining.stream().mapToInt(GalaxyCosmeticRender::note).max().orElse(0);
            baseStructureRepository.updateMaxNote(baseId, maxNote);
        }
    }

    @Override
    public void deleteRendersByBase(UUID baseId) {
        List<GalaxyCosmeticRender> renders = cosmeticRenderRepository.findAllByBaseStructureId(baseId);
        renders.forEach(r -> cosmeticRenderRepository.deleteById(r.id()));
        baseStructureRepository.deleteById(baseId);
    }

    @Override
    public List<GalaxyCosmeticRender> reapplyCosmetics(UUID baseId, GalaxyRequestCmd newBaseParams) throws IOException {
        GalaxyBaseStructure oldBase = baseStructureRepository.findById(baseId)
                .orElseThrow(() -> new IllegalArgumentException("Base not found: " + baseId));

        List<GalaxyCosmeticRender> existingRenders = cosmeticRenderRepository.findAllByBaseStructureId(baseId);

        // Supprimer l'ancienne base et ses rendus (ils vont être recréés avec le nouveau configHash)
        deleteRendersByBase(baseId);

        // Créer la nouvelle base
        GalaxyBaseStructure newBase = findOrCreateBase(newBaseParams);

        // Recréer chaque rendu avec les nouveaux paramètres de base mais les anciennes cosmétiques
        for (GalaxyCosmeticRender oldRender : existingRenders) {
            GalaxyRequestCmd reapplyCmd = GalaxyRequestCmd.builder()
                    .width(newBase.width())
                    .height(newBase.height())
                    .seed(newBase.seed())
                    .galaxyType(newBase.galaxyType())
                    .coreSize(newBase.coreSize())
                    .galaxyRadius(newBase.galaxyRadius())
                    .warpStrength(newBase.warpStrength())
                    .noiseParameters(new NoiseParameters(newBase.noiseOctaves(), newBase.noisePersistence(), newBase.noiseLacunarity(), newBase.noiseScale()))
                    .multiLayerNoiseParameters(new MultiLayerNoiseParameters(newBase.multiLayerEnabled(), newBase.macroLayerScale(), newBase.macroLayerWeight(), newBase.mesoLayerScale(), newBase.mesoLayerWeight(), newBase.microLayerScale(), newBase.microLayerWeight()))
                    // On récupère les paramètres de structure sérialisés (attention, ici on suppose qu'ils n'ont pas changé de format)
                    // En fait, on utilise newBaseParams pour les paramètres de structure car ils sont fournis dans la commande
                    .spiralParameters(newBaseParams.getSpiralParameters())
                    .voronoiParameters(newBaseParams.getVoronoiParameters())
                    .ellipticalParameters(newBaseParams.getEllipticalParameters())
                    .ringParameters(newBaseParams.getRingParameters())
                    .irregularParameters(newBaseParams.getIrregularParameters())
                    // Cosmétiques de l'ancien rendu
                    .note(oldRender.note())
                    .colorParameters(ColorParameters.builder()
                            .colorPalette(oldRender.colorPalette())
                            .spaceBackgroundColor(oldRender.spaceBackgroundColor())
                            .coreColor(oldRender.coreColor())
                            .armColor(oldRender.armColor())
                            .outerColor(oldRender.outerColor())
                            .build())
                    .bloomParameters(BloomParameters.builder()
                            .enabled(oldRender.bloomEnabled())
                            .bloomRadius((int) oldRender.bloomRadius())
                            .bloomIntensity(oldRender.bloomIntensity())
                            .bloomThreshold(oldRender.bloomThreshold())
                            .build())
                    .starFieldParameters(StarFieldParameters.builder()
                            .enabled(oldRender.starFieldEnabled())
                            .density(oldRender.starDensity())
                            .maxStarSize((int) oldRender.maxStarSize())
                            .diffractionSpikes(oldRender.diffractionSpikes())
                            .spikeCount(oldRender.spikeCount())
                            .build())
                    .build();

            // Créer le nouveau rendu (le thumbnail sera recalculé avec la nouvelle structure)
            rate(reapplyCmd);
        }

        return findRendersByBaseId(newBase.id());
    }

    private void validateNote(int note) {
        if (note < 1 || note > 5) {
            throw new IllegalArgumentException("Note must be between 1 and 5, got: " + note);
        }
    }

    private byte[] buildThumbnail(GalaxyRequestCmd cmd) throws IOException {
        GalaxyRequestCmd thumbnailCmd = GalaxyRequestCmd.builder()
                .width(200).height(200)
                .seed(cmd.getSeed())
                .galaxyType(cmd.getGalaxyType())
                .coreSize(cmd.getCoreSize())
                .galaxyRadius(cmd.getGalaxyRadius() != null ? Math.min(cmd.getGalaxyRadius(), 100.0) : 100.0)
                .warpStrength(cmd.getWarpStrength())
                .noiseParameters(cmd.getNoiseParameters())
                .spiralParameters(cmd.getSpiralParameters())
                .voronoiParameters(cmd.getVoronoiParameters())
                .ellipticalParameters(cmd.getEllipticalParameters())
                .ringParameters(cmd.getRingParameters())
                .irregularParameters(cmd.getIrregularParameters())
                .starFieldParameters(StarFieldParameters.noStars())
                .multiLayerNoiseParameters(cmd.getMultiLayerNoiseParameters())
                .bloomParameters(BloomParameters.disabled())
                .colorParameters(cmd.getColorParameters())
                .build();
        BufferedImage image = generateGalaxyBufferedImage(thumbnailCmd);
        return imageSerializer.toByteArray(image);
    }

    private GalaxyBaseStructure findOrCreateBase(GalaxyRequestCmd cmd) {
        int configHash = computeConfigHash(cmd);
        return baseStructureRepository.findByConfigHash(configHash)
                .orElseGet(() -> {
                    GalaxyBaseStructure newBase = buildBaseStructure(cmd, configHash);
                    return baseStructureRepository.save(newBase);
                });
    }

    private GalaxyCosmeticRender findOrCreateRender(GalaxyRequestCmd cmd, GalaxyBaseStructure base, byte[] thumbnail) {
        int cosmeticHash = computeCosmeticHash(cmd);
        Optional<GalaxyCosmeticRender> existing = cosmeticRenderRepository
                .findByBaseStructureIdAndCosmeticHash(base.id(), cosmeticHash);

        return existing.isPresent()
                ? updateWithNewNote(existing.get(), cmd.getNote(), thumbnail)
                : createNewRender(cmd, base, thumbnail);
    }

    private GalaxyCosmeticRender updateWithNewNote(GalaxyCosmeticRender existing, int note, byte[] thumbnail) {
        return new GalaxyCosmeticRender(existing.id(), existing.baseStructureId(), existing.description(),
                note, thumbnail, existing.colorPalette(), existing.spaceBackgroundColor(),
                existing.coreColor(), existing.armColor(), existing.outerColor(),
                existing.bloomEnabled(), existing.bloomRadius(), existing.bloomIntensity(), existing.bloomThreshold(),
                existing.starFieldEnabled(), existing.starDensity(), existing.maxStarSize(),
                existing.diffractionSpikes(), existing.spikeCount());
    }

    private GalaxyCosmeticRender createNewRender(GalaxyRequestCmd cmd, GalaxyBaseStructure base, byte[] thumbnail) {
        ColorParameters color = cmd.getColorParameters();
        BloomParameters bloom = cmd.getBloomParameters();
        StarFieldParameters stars = cmd.getStarFieldParameters();

        GalaxyCosmeticRender template = new GalaxyCosmeticRender(null, base.id(), null, cmd.getNote(), thumbnail,
                color.colorPalette(), color.spaceBackgroundColor(), color.coreColor(), color.armColor(), color.outerColor(),
                bloom.enabled(), bloom.bloomRadius(), bloom.bloomIntensity(), bloom.bloomThreshold(),
                stars.enabled(), stars.density(), stars.maxStarSize(), stars.diffractionSpikes(), stars.spikeCount());

        return new GalaxyCosmeticRender(UUID.randomUUID(), base.id(), template.generateDescription(), cmd.getNote(),
                thumbnail, color.colorPalette(), color.spaceBackgroundColor(), color.coreColor(), color.armColor(), color.outerColor(),
                bloom.enabled(), bloom.bloomRadius(), bloom.bloomIntensity(), bloom.bloomThreshold(),
                stars.enabled(), stars.density(), stars.maxStarSize(), stars.diffractionSpikes(), stars.spikeCount());
    }

    private void recalculateMaxNote(UUID baseId) {
        int maxNote = cosmeticRenderRepository.findAllByBaseStructureId(baseId).stream()
                .mapToInt(GalaxyCosmeticRender::note).max().orElse(0);
        baseStructureRepository.updateMaxNote(baseId, maxNote);
    }

    private int computeConfigHash(GalaxyRequestCmd cmd) {
        return buildBaseStructure(cmd, 0).configHash();
    }

    private int computeCosmeticHash(GalaxyRequestCmd cmd) {
        ColorParameters color = cmd.getColorParameters();
        BloomParameters bloom = cmd.getBloomParameters();
        StarFieldParameters stars = cmd.getStarFieldParameters();
        return new GalaxyCosmeticRender(null, null, null, 0, null,
                color.colorPalette(), color.spaceBackgroundColor(), color.coreColor(), color.armColor(), color.outerColor(),
                bloom.enabled(), bloom.bloomRadius(), bloom.bloomIntensity(), bloom.bloomThreshold(),
                stars.enabled(), stars.density(), stars.maxStarSize(), stars.diffractionSpikes(), stars.spikeCount())
                .cosmeticHash();
    }

    private GalaxyBaseStructure buildBaseStructure(GalaxyRequestCmd cmd, int ignoredHash) {
        MultiLayerNoiseParameters ml = cmd.getMultiLayerNoiseParameters();
        NoiseParameters noise = cmd.getNoiseParameters();
        String structureParams = serializeStructureParams(cmd);

        GalaxyBaseStructure template = new GalaxyBaseStructure(null, null, 0,
                cmd.getWidth(), cmd.getHeight(), cmd.getSeed(), cmd.getGalaxyType(),
                cmd.getCoreSize() != null ? cmd.getCoreSize() : 0.05,
                cmd.getGalaxyRadius() != null ? cmd.getGalaxyRadius() : 1500.0,
                cmd.getWarpStrength(),
                noise.octaves(), noise.persistence(), noise.lacunarity(), noise.scale(),
                ml.enabled(), ml.macroLayerScale(), ml.macroLayerWeight(),
                ml.mesoLayerScale(), ml.mesoLayerWeight(), ml.microLayerScale(), ml.microLayerWeight(),
                structureParams);

        return new GalaxyBaseStructure(UUID.randomUUID(), template.generateDescription(), 0,
                cmd.getWidth(), cmd.getHeight(), cmd.getSeed(), cmd.getGalaxyType(),
                cmd.getCoreSize() != null ? cmd.getCoreSize() : 0.05,
                cmd.getGalaxyRadius() != null ? cmd.getGalaxyRadius() : 1500.0,
                cmd.getWarpStrength(),
                noise.octaves(), noise.persistence(), noise.lacunarity(), noise.scale(),
                ml.enabled(), ml.macroLayerScale(), ml.macroLayerWeight(),
                ml.mesoLayerScale(), ml.mesoLayerWeight(), ml.microLayerScale(), ml.microLayerWeight(),
                structureParams);
    }

    private String serializeStructureParams(GalaxyRequestCmd cmd) {
        Object typeParams = switch (cmd.getGalaxyType() != null ? cmd.getGalaxyType() : "SPIRAL") {
            case "SPIRAL" -> cmd.getSpiralParameters();
            case "VORONOI_CLUSTER" -> cmd.getVoronoiParameters();
            case "ELLIPTICAL", "LENTICULAR" -> cmd.getEllipticalParameters();
            case "RING" -> cmd.getRingParameters();
            case "IRREGULAR" -> cmd.getIrregularParameters();
            default -> cmd.getSpiralParameters();
        };
        return typeParams != null ? typeParams.toString() : null;
    }

    private BufferedImage generateGalaxyBufferedImage(GalaxyRequestCmd cmd) {
        int configHash = computeConfigHash(cmd);
        return galaxyImageComputationPort.computeImage(configHash, cmd);
    }
}
