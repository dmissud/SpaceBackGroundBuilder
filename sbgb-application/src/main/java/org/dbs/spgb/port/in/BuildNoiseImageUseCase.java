package org.dbs.spgb.port.in;

import java.io.IOException;

@FunctionalInterface
public interface BuildNoiseImageUseCase {
    byte[] buildNoiseImage(ImageRequestCmd imageRequestCmd) throws IOException;
}