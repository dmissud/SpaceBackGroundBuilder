package org.dbs.sbgb.port.in;

import org.dbs.sbgb.domain.model.GalaxyCosmeticRender;

import java.util.List;
import java.util.UUID;

public interface FindGalaxyCosmeticRendersUseCase {
    List<GalaxyCosmeticRender> findRendersByBaseId(UUID baseId);
}
