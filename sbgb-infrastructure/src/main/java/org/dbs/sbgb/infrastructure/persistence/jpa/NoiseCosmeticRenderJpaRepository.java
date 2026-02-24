package org.dbs.sbgb.infrastructure.persistence.jpa;

import org.dbs.sbgb.infrastructure.persistence.entity.NoiseCosmeticRenderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoiseCosmeticRenderJpaRepository extends JpaRepository<NoiseCosmeticRenderEntity, UUID> {
    Optional<NoiseCosmeticRenderEntity> findByBaseStructureIdAndCosmeticHash(UUID baseStructureId, int cosmeticHash);

    List<NoiseCosmeticRenderEntity> findAllByBaseStructureId(UUID baseStructureId);
}
