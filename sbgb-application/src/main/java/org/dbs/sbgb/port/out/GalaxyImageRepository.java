package org.dbs.sbgb.port.out;

import org.dbs.sbgb.domain.model.GalaxyImage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GalaxyImageRepository {
    GalaxyImage save(GalaxyImage galaxyImage);

    List<GalaxyImage> findAll();

    GalaxyImage findById(UUID id);

    Optional<GalaxyImage> findByName(String name);
}
