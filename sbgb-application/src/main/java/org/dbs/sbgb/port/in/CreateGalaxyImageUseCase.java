package org.dbs.sbgb.port.in;

import org.dbs.sbgb.domain.model.GalaxyImage;

import java.io.IOException;

public interface CreateGalaxyImageUseCase {
    GalaxyImage createGalaxyImage(GalaxyRequestCmd galaxyRequestCmd) throws IOException;
}
