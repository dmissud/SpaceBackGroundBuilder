package org.dbs.sbgb.infrastructure.persistence.jpa;

import org.dbs.sbgb.domain.model.NoiseImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoiseImageJpaRepository extends JpaRepository<NoiseImage, UUID> {
    Optional<NoiseImage> findByName(String name);
}
