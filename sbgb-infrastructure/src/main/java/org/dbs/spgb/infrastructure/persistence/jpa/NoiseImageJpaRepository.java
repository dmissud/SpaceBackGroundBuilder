package org.dbs.spgb.infrastructure.persistence.jpa;

import org.dbs.spgb.domain.model.NoiseImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NoiseImageJpaRepository extends JpaRepository<NoiseImage, UUID> {
}
