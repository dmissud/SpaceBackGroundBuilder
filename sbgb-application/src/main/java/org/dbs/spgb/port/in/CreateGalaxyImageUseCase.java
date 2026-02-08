package org.dbs.spgb.port.in;

import org.dbs.spgb.domain.model.GalaxyImage;

import java.io.IOException;

public interface CreateGalaxyImageUseCase {
    GalaxyImage createGalaxyImage(GalaxyRequestCmd galaxyRequestCmd) throws IOException;
}
