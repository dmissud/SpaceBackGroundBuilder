package org.dbs.sbgb.infrastructure.persistence.jpa;

import org.dbs.sbgb.domain.model.GalaxyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GalaxyImageJpaRepository extends JpaRepository<GalaxyImage, UUID> {
    Optional<GalaxyImage> findByName(String name);
}
