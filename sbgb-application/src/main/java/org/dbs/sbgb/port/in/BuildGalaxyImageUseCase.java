package org.dbs.sbgb.port.in;

import java.io.IOException;

public interface BuildGalaxyImageUseCase {
    byte[] buildGalaxyImage(GalaxyRequestCmd galaxyRequestCmd) throws IOException;
}
