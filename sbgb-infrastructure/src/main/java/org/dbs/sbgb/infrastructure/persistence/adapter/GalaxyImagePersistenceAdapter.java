package org.dbs.sbgb.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.dbs.sbgb.domain.model.GalaxyImage;
import org.dbs.sbgb.infrastructure.persistence.jpa.GalaxyImageJpaRepository;
import org.dbs.sbgb.port.out.GalaxyImageRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GalaxyImagePersistenceAdapter implements GalaxyImageRepository {

    private final GalaxyImageJpaRepository galaxyImageJpaRepository;

    @Override
    @Transactional
    public GalaxyImage save(GalaxyImage galaxyImage) {
        return galaxyImageJpaRepository.save(galaxyImage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GalaxyImage> findAll() {
        return galaxyImageJpaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public GalaxyImage findById(UUID id) {
        return galaxyImageJpaRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GalaxyImage> findByName(String name) {
        return galaxyImageJpaRepository.findByName(name);
    }
}
