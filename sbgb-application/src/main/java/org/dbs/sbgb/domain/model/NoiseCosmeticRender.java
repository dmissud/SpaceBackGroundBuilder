package org.dbs.sbgb.domain.model;

import java.util.Objects;
import java.util.UUID;

public record NoiseCosmeticRender(
        UUID id,
        UUID baseStructureId,
        String back,
        String middle,
        String fore,
        double backThreshold,
        double middleThreshold,
        String interpolationType,
        boolean transparentBackground,
        int note,
        byte[] thumbnail,
        String description
) {
    public int cosmeticHash() {
        return Objects.hash(back, middle, fore, backThreshold, middleThreshold, interpolationType, transparentBackground);
    }

    public String generateDescription() {
        return "Colors: %s / %s / %s (%s)".formatted(back, middle, fore, interpolationType);
    }
}
