package org.dbs.sbgb.infrastructure.persistence.jpa;

import org.dbs.sbgb.infrastructure.persistence.entity.NoiseImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoiseImageJpaRepository extends JpaRepository<NoiseImageEntity, UUID> {
    Optional<NoiseImageEntity> findByName(String name);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE NoiseImageEntity n SET n.note = :note WHERE n.id = :id")
    void updateNote(@Param("id") UUID id, @Param("note") int note);
}
