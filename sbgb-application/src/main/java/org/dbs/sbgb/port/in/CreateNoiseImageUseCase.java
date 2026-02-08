package org.dbs.sbgb.port.in;

import org.dbs.sbgb.domain.model.NoiseImage;

import java.io.IOException;

public interface CreateNoiseImageUseCase {
    //
    NoiseImage createNoiseImage(ImageRequestCmd imageRequestCmd) throws IOException;
}