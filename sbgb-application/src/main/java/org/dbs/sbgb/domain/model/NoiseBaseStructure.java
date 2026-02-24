package org.dbs.sbgb.domain.model;

import java.util.Objects;
import java.util.UUID;

public record NoiseBaseStructure(
        UUID id,
        String description,
        int maxNote,
        int width,
        int height,
        int seed,
        int octaves,
        double persistence,
        double lacunarity,
        double scale,
        String noiseType,
        boolean useMultiLayer,
        String layersConfig
) {
    public int configHash() {
        return Objects.hash(width, height, seed, octaves, persistence, lacunarity, scale, noiseType, useMultiLayer, layersConfig);
    }

    public String generateDescription() {
        return "Noise %s %dx%d seed=%d octaves=%d".formatted(noiseType, width, height, seed, octaves);
    }
}
