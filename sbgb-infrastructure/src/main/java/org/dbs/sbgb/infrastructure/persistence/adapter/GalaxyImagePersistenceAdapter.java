package org.dbs.sbgb.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.dbs.sbgb.domain.model.GalaxyImage;
import org.dbs.sbgb.infrastructure.persistence.entity.GalaxyImageEntity;
import org.dbs.sbgb.infrastructure.persistence.jpa.GalaxyImageJpaRepository;
import org.dbs.sbgb.infrastructure.persistence.mapper.GalaxyEntityMapper;
import org.dbs.sbgb.port.out.GalaxyImageRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GalaxyImagePersistenceAdapter implements GalaxyImageRepository {

    private final GalaxyImageJpaRepository galaxyImageJpaRepository;
    private final GalaxyEntityMapper mapper;

    @Override
    @Transactional
    public GalaxyImage save(GalaxyImage galaxyImage) {
        GalaxyImageEntity entity = mapper.toEntity(galaxyImage);
        GalaxyImageEntity saved = galaxyImageJpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GalaxyImage> findAll() {
        return galaxyImageJpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GalaxyImage findById(UUID id) {
        return galaxyImageJpaRepository.findById(id).map(mapper::toDomain).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GalaxyImage> findByName(String name) {
        return galaxyImageJpaRepository.findByName(name).map(mapper::toDomain);
    }
}
