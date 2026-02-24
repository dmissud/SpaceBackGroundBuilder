package org.dbs.sbgb.port.in;

import org.dbs.sbgb.domain.model.NoiseImage;

import java.util.List;

public interface FindNoiseImagesUseCase {
    List<NoiseImage> findAll();
}
