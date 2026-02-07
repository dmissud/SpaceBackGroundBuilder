package org.dbs.spgb.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.dbs.spgb.domain.model.GalaxyImage;
import org.dbs.spgb.infrastructure.persistence.jpa.GalaxyImageJpaRepository;
import org.dbs.spgb.port.out.GalaxyImageRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
}
