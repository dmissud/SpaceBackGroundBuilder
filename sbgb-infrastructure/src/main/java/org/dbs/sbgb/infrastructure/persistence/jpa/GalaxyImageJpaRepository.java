package org.dbs.sbgb.infrastructure.persistence.jpa;

import org.dbs.sbgb.infrastructure.persistence.entity.GalaxyImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GalaxyImageJpaRepository extends JpaRepository<GalaxyImageEntity, UUID> {
    Optional<GalaxyImageEntity> findByName(String name);
}
