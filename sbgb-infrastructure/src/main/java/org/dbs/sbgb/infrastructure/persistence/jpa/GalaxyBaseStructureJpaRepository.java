package org.dbs.sbgb.infrastructure.persistence.jpa;

import org.dbs.sbgb.infrastructure.persistence.entity.GalaxyBaseStructureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface GalaxyBaseStructureJpaRepository extends JpaRepository<GalaxyBaseStructureEntity, UUID> {

    Optional<GalaxyBaseStructureEntity> findByConfigHash(int configHash);

    @Modifying
    @Query("UPDATE GalaxyBaseStructureEntity e SET e.maxNote = :maxNote WHERE e.id = :id")
    void updateMaxNote(@Param("id") UUID id, @Param("maxNote") int maxNote);
}
