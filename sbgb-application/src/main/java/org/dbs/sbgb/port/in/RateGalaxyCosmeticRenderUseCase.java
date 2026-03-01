package org.dbs.sbgb.port.in;

import org.dbs.sbgb.domain.model.GalaxyCosmeticRender;

import java.io.IOException;

public interface RateGalaxyCosmeticRenderUseCase {
    GalaxyCosmeticRender rate(GalaxyRequestCmd cmd) throws IOException;
}
