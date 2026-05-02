package org.dbs.sbgb.port.in;

import java.util.List;

public interface GetGalaxyParticlesUseCase {
    List<StarParticleDto> getParticles(GalaxyRequestCmd cmd);
}
