package org.dbs.sbgb.port.in;

import org.dbs.sbgb.domain.model.NoiseCosmeticRender;

import java.util.List;
import java.util.UUID;

public interface FindNoiseCosmeticRendersUseCase {
    List<NoiseCosmeticRender> findRendersByBaseId(UUID baseId);
}