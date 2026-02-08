package org.dbs.sbgb.port.in;

import org.dbs.sbgb.domain.model.GalaxyImage;

import java.util.List;

public interface FindGalaxyImagesUseCase {
    List<GalaxyImage> findAllGalaxyImages();
}
