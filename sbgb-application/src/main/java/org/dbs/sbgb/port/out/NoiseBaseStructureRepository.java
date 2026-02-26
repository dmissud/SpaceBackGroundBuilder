package org.dbs.sbgb.port.out;

import org.dbs.sbgb.domain.model.NoiseBaseStructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoiseBaseStructureRepository {
    NoiseBaseStructure save(NoiseBaseStructure structure);

    List<NoiseBaseStructure> findAll();

    Optional<NoiseBaseStructure> findByConfigHash(int hash);

    void deleteById(UUID id);

    NoiseBaseStructure updateMaxNote(UUID id, int maxNote);
}
