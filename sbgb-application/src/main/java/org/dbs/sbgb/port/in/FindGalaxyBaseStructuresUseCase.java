package org.dbs.sbgb.port.in;

import org.dbs.sbgb.domain.model.GalaxyBaseStructure;

import java.util.List;

public interface FindGalaxyBaseStructuresUseCase {
    List<GalaxyBaseStructure> findAllSortedByMaxNoteDesc();
}
