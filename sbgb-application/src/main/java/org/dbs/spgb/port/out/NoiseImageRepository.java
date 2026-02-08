package org.dbs.spgb.port.out;


import org.dbs.spgb.domain.model.NoiseImage;

import java.util.List;
import java.util.Optional;

public interface NoiseImageRepository {
    NoiseImage save(NoiseImage noiseImage);

    List<NoiseImage> findAll();

    Optional<NoiseImage> findByName(String name);
}