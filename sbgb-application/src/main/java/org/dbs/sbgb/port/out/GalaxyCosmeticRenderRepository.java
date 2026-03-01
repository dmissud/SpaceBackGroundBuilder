package org.dbs.sbgb.port.out;

import org.dbs.sbgb.domain.model.GalaxyCosmeticRender;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GalaxyCosmeticRenderRepository {
    GalaxyCosmeticRender save(GalaxyCosmeticRender render);

    void deleteById(UUID id);

    Optional<GalaxyCosmeticRender> findById(UUID id);

    Optional<GalaxyCosmeticRender> findByBaseStructureIdAndCosmeticHash(UUID baseId, int cosmeticHash);

    List<GalaxyCosmeticRender> findAllByBaseStructureId(UUID baseId);
}
