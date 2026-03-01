package org.dbs.sbgb.port.in;

import java.util.UUID;

public interface DeleteRendersByBaseUseCase {
    void deleteRendersByBase(UUID baseId);
}
