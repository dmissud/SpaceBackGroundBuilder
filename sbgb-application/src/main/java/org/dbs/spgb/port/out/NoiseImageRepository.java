package org.dbs.spgb.port.out;


import org.dbs.spgb.domain.model.NoiseImage;

public interface NoiseImageRepository {
    NoiseImage save(NoiseImage noiseImage);
}