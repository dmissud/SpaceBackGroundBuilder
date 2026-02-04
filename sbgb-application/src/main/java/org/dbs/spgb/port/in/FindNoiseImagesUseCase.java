package org.dbs.spgb.port.in;

import org.dbs.spgb.domain.model.NoiseImage;

import java.util.List;

public interface FindNoiseImagesUseCase {
    List<NoiseImage> findAll();
}
