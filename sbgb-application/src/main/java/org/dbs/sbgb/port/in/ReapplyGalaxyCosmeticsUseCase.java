package org.dbs.sbgb.port.in;

import org.dbs.sbgb.domain.model.GalaxyCosmeticRender;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ReapplyGalaxyCosmeticsUseCase {
    List<GalaxyCosmeticRender> reapplyCosmetics(UUID baseId, GalaxyRequestCmd newBaseParams) throws IOException;
}
