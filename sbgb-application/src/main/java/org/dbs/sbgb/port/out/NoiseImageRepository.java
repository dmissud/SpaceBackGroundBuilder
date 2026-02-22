package org.dbs.sbgb.port.out;


import org.dbs.sbgb.domain.model.NoiseImage;

import java.util.UUID;

import java.util.List;
import java.util.Optional;

public interface NoiseImageRepository {
    NoiseImage save(NoiseImage noiseImage);

    List<NoiseImage> findAll();

    Optional<NoiseImage> findByName(String name);

    void updateNote(UUID id, int note);
}