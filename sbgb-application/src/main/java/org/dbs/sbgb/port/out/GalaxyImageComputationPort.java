package org.dbs.sbgb.port.out;

import org.dbs.sbgb.port.in.GalaxyRequestCmd;
import java.awt.image.BufferedImage;

public interface GalaxyImageComputationPort {
    BufferedImage computeImage(int configHash, GalaxyRequestCmd cmd);
}
