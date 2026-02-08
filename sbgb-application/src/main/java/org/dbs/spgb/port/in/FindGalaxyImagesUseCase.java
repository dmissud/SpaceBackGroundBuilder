package org.dbs.spgb.port.in;

import org.dbs.spgb.domain.model.GalaxyImage;

import java.util.List;

public interface FindGalaxyImagesUseCase {
    List<GalaxyImage> findAllGalaxyImages();
}
