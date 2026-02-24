package org.dbs.sbgb.port.in;

import org.dbs.sbgb.domain.model.NoiseBaseStructure;

import java.util.List;

public interface FindNoiseBaseStructuresUseCase {
    List<NoiseBaseStructure> findAllSortedByMaxNoteDesc();
}
