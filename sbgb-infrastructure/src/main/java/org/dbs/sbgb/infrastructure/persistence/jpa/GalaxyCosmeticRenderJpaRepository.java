package org.dbs.sbgb.infrastructure.persistence.jpa;

import org.dbs.sbgb.infrastructure.persistence.entity.GalaxyCosmeticRenderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GalaxyCosmeticRenderJpaRepository extends JpaRepository<GalaxyCosmeticRenderEntity, UUID> {

    Optional<GalaxyCosmeticRenderEntity> findByBaseStructureIdAndCosmeticHash(UUID baseStructureId, int cosmeticHash);

    List<GalaxyCosmeticRenderEntity> findAllByBaseStructureId(UUID baseStructureId);
}
