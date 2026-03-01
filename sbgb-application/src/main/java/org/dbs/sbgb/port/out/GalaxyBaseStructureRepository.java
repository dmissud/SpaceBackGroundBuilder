package org.dbs.sbgb.port.out;

import org.dbs.sbgb.domain.model.GalaxyBaseStructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GalaxyBaseStructureRepository {
    GalaxyBaseStructure save(GalaxyBaseStructure base);

    List<GalaxyBaseStructure> findAll();

    Optional<GalaxyBaseStructure> findByConfigHash(int hash);

    Optional<GalaxyBaseStructure> findById(UUID id);

    void deleteById(UUID id);

    GalaxyBaseStructure updateMaxNote(UUID id, int maxNote);
}
