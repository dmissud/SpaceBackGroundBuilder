package org.dbs.spgb.domain.model;

import java.awt.*;

public interface NoiseColorCalculator {
    Color calculateNoiseColor(double noiseVal);

    Color getBackGroundColor();
}
