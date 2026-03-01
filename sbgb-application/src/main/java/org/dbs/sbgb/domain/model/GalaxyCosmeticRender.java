package org.dbs.sbgb.domain.model;

import java.util.Objects;
import java.util.UUID;

public record GalaxyCosmeticRender(
        UUID id,
        UUID baseStructureId,
        String description,
        int note,
        byte[] thumbnail,
        String colorPalette,
        String spaceBackgroundColor,
        String coreColor,
        String armColor,
        String outerColor,
        boolean bloomEnabled,
        double bloomRadius,
        double bloomIntensity,
        double bloomThreshold,
        boolean starFieldEnabled,
        double starDensity,
        double maxStarSize,
        boolean diffractionSpikes,
        int spikeCount
) {
    public int cosmeticHash() {
        return Objects.hash(colorPalette, spaceBackgroundColor, coreColor, armColor, outerColor,
                bloomEnabled, bloomRadius, bloomIntensity, bloomThreshold,
                starFieldEnabled, starDensity, maxStarSize, diffractionSpikes, spikeCount);
    }

    public String generateDescription() {
        String bloomPart = bloomEnabled
                ? "Bloom r=%.0f".formatted(bloomRadius)
                : "Sans bloom";
        String starPart = starFieldEnabled
                ? "%.0f étoiles".formatted(starDensity * 10000)
                : "Sans étoiles";
        String palette = colorPalette != null ? colorPalette : "Personnalisé";
        return "%s — %s — %s".formatted(palette, bloomPart, starPart);
    }
}
