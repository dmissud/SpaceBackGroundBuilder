package org.dbs.sbgb.port.in;

import org.dbs.sbgb.domain.model.NoiseCosmeticRender;

import java.io.IOException;

public interface RateNoiseCosmeticRenderUseCase {
    NoiseCosmeticRender rate(ImageRequestCmd cmd) throws IOException;
}
