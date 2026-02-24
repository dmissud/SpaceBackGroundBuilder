package org.dbs.sbgb.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NoiseCosmeticRenderTest {

    @Test
    void shouldProduceSameCosmeticHashForIdenticalParams() {
        UUID baseId = UUID.randomUUID();
        NoiseCosmeticRender render1 = buildRender(UUID.randomUUID(), baseId, "#000000", "#888888", "#FFFFFF", 0.4, 0.7, "LINEAR", false, 3);
        NoiseCosmeticRender render2 = buildRender(UUID.randomUUID(), baseId, "#000000", "#888888", "#FFFFFF", 0.4, 0.7, "LINEAR", false, 5);

        assertThat(render1.cosmeticHash()).isEqualTo(render2.cosmeticHash());
    }

    @Test
    void shouldProduceDifferentCosmeticHashForDifferentColors() {
        UUID baseId = UUID.randomUUID();
        NoiseCosmeticRender render1 = buildRender(UUID.randomUUID(), baseId, "#000000", "#888888", "#FFFFFF", 0.4, 0.7, "LINEAR", false, 3);
        NoiseCosmeticRender render2 = buildRender(UUID.randomUUID(), baseId, "#FF0000", "#888888", "#FFFFFF", 0.4, 0.7, "LINEAR", false, 3);

        assertThat(render1.cosmeticHash()).isNotEqualTo(render2.cosmeticHash());
    }

    @Test
    void shouldIgnoreIdAndBaseStructureIdAndNoteAndThumbnailInCosmeticHash() {
        UUID baseId1 = UUID.randomUUID();
        UUID baseId2 = UUID.randomUUID();
        NoiseCosmeticRender render1 = new NoiseCosmeticRender(UUID.randomUUID(), baseId1, "#000000", "#888888", "#FFFFFF", 0.4, 0.7, "LINEAR", false, 1, new byte[]{1}, "desc1");
        NoiseCosmeticRender render2 = new NoiseCosmeticRender(UUID.randomUUID(), baseId2, "#000000", "#888888", "#FFFFFF", 0.4, 0.7, "LINEAR", false, 5, new byte[]{2, 3}, "desc2");

        assertThat(render1.cosmeticHash()).isEqualTo(render2.cosmeticHash());
    }

    @Test
    void shouldProduceStableCosmeticHash() {
        NoiseCosmeticRender render = buildRender(UUID.randomUUID(), UUID.randomUUID(), "#000000", "#888888", "#FFFFFF", 0.4, 0.7, "SMOOTHSTEP", false, 4);

        assertThat(render.cosmeticHash()).isEqualTo(render.cosmeticHash());
    }

    @Test
    void shouldGenerateNonNullDescription() {
        NoiseCosmeticRender render = buildRender(UUID.randomUUID(), UUID.randomUUID(), "#000000", "#888888", "#FFFFFF", 0.4, 0.7, "LINEAR", false, 3);

        assertThat(render.generateDescription()).isNotNull().isNotBlank();
    }

    private NoiseCosmeticRender buildRender(UUID id, UUID baseId, String back, String middle, String fore,
                                             double backThreshold, double middleThreshold, String interpolationType,
                                             boolean transparentBackground, int note) {
        return new NoiseCosmeticRender(id, baseId, back, middle, fore, backThreshold, middleThreshold,
                interpolationType, transparentBackground, note, null, "description");
    }
}
