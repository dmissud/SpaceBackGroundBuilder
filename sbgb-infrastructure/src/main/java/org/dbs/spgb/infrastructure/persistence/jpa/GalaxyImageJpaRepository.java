package org.dbs.spgb.infrastructure.persistence.jpa;

import org.dbs.spgb.domain.model.GalaxyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GalaxyImageJpaRepository extends JpaRepository<GalaxyImage, UUID> {
    Optional<GalaxyImage> findByName(String name);
}
