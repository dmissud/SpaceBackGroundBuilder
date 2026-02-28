package org.dbs.sbgb.domain.model;

import java.util.Objects;
import java.util.UUID;

public record GalaxyBaseStructure(
        UUID id,
        String description,
        int maxNote,
        int width,
        int height,
        long seed,
        String galaxyType,
        double coreSize,
        double galaxyRadius,
        double warpStrength,
        int noiseOctaves,
        double noisePersistence,
        double noiseLacunarity,
        double noiseScale,
        boolean multiLayerEnabled,
        double macroLayerScale,
        double macroLayerWeight,
        double mesoLayerScale,
        double mesoLayerWeight,
        double microLayerScale,
        double microLayerWeight,
        String structureParams
) {
    public int configHash() {
        return Objects.hash(width, height, seed, galaxyType, coreSize, galaxyRadius, warpStrength,
                noiseOctaves, noisePersistence, noiseLacunarity, noiseScale,
                multiLayerEnabled, macroLayerScale, macroLayerWeight,
                mesoLayerScale, mesoLayerWeight, microLayerScale, microLayerWeight,
                structureParams);
    }

    public String generateDescription() {
        return switch (galaxyType) {
            case "SPIRAL" -> "Spirale — %dx%d, seed %d".formatted(width, height, seed);
            case "VORONOI_CLUSTER" -> "Voronoï — %dx%d, seed %d".formatted(width, height, seed);
            case "ELLIPTICAL" -> "Elliptique — %dx%d, seed %d".formatted(width, height, seed);
            case "LENTICULAR" -> "Lenticulaire — %dx%d, seed %d".formatted(width, height, seed);
            case "RING" -> "Anneau — %dx%d, seed %d".formatted(width, height, seed);
            case "IRREGULAR" -> "Irrégulière — %dx%d, seed %d".formatted(width, height, seed);
            default -> "%s — %dx%d, seed %d".formatted(galaxyType, width, height, seed);
        };
    }
}
