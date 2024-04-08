package org.dbs.spgb.port.in;

import org.dbs.spgb.domain.model.NoiseImage;

import java.io.IOException;

@FunctionalInterface
public interface CreateNoiseImageUseCase {
    //
    NoiseImage createNoiseImage(ImageRequestCmd imageRequestCmd) throws IOException;
}