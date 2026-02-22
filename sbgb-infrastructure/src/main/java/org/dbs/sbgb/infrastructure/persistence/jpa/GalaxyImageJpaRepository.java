package org.dbs.sbgb.infrastructure.persistence.jpa;

import org.dbs.sbgb.infrastructure.persistence.entity.GalaxyImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GalaxyImageJpaRepository extends JpaRepository<GalaxyImageEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE GalaxyImageEntity g SET g.note = :note WHERE g.id = :id")
    void updateNote(@Param("id") UUID id, @Param("note") int note);
}
