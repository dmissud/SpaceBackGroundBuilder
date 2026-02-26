package org.dbs.sbgb.port.out;

import org.dbs.sbgb.domain.model.NoiseCosmeticRender;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoiseCosmeticRenderRepository {
    NoiseCosmeticRender save(NoiseCosmeticRender render);

    void deleteById(UUID id);

    Optional<NoiseCosmeticRender> findById(UUID id);

    Optional<NoiseCosmeticRender> findByBaseStructureIdAndCosmeticHash(UUID baseStructureId, int cosmeticHash);

    List<NoiseCosmeticRender> findAllByBaseStructureId(UUID baseStructureId);
}
