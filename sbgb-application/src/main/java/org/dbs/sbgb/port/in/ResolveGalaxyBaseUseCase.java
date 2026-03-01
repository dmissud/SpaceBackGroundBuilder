package org.dbs.sbgb.port.in;

import org.dbs.sbgb.domain.model.GalaxyBaseStructure;

import java.util.Optional;

public interface ResolveGalaxyBaseUseCase {
    Optional<GalaxyBaseStructure> resolveBase(GalaxyRequestCmd cmd);
}
