package org.dbs.sbgb.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.dbs.sbgb.domain.model.NoiseImage;
import org.dbs.sbgb.infrastructure.persistence.entity.NoiseImageEntity;
import org.dbs.sbgb.infrastructure.persistence.jpa.NoiseImageJpaRepository;
import org.dbs.sbgb.infrastructure.persistence.mapper.NoiseEntityMapper;
import org.dbs.sbgb.port.out.NoiseImageRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NoiseImagePersistenceAdapter implements NoiseImageRepository {

    private final NoiseImageJpaRepository noiseImageJpaRepository;
    private final NoiseEntityMapper mapper;

    @Override
    @Transactional
    public NoiseImage save(NoiseImage noiseImage) {
        NoiseImageEntity entity = mapper.toEntity(noiseImage);
        NoiseImageEntity saved = noiseImageJpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoiseImage> findAll() {
        return noiseImageJpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NoiseImage> findByName(String name) {
        return noiseImageJpaRepository.findByName(name).map(mapper::toDomain);
    }
}
