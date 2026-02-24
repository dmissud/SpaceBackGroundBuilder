package org.dbs.sbgb.port.in;

import java.io.IOException;

public interface BuildNoiseImageUseCase {
    byte[] buildNoiseImage(ImageRequestCmd imageRequestCmd) throws IOException;
}