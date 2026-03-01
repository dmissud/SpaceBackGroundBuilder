package org.dbs.sbgb.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.dbs.sbgb.domain.model.GalaxyBaseStructure;
import org.dbs.sbgb.infrastructure.persistence.jpa.GalaxyBaseStructureJpaRepository;
import org.dbs.sbgb.infrastructure.persistence.mapper.GalaxyBaseStructureMapper;
import org.dbs.sbgb.port.out.GalaxyBaseStructureRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GalaxyBaseStructurePersistenceAdapter implements GalaxyBaseStructureRepository {

    private final GalaxyBaseStructureJpaRepository jpaRepository;
    private final GalaxyBaseStructureMapper mapper;

    @Override
    @Transactional
    public GalaxyBaseStructure save(GalaxyBaseStructure base) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(base)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GalaxyBaseStructure> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GalaxyBaseStructure> findByConfigHash(int hash) {
        return jpaRepository.findByConfigHash(hash).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GalaxyBaseStructure> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public GalaxyBaseStructure updateMaxNote(UUID id, int maxNote) {
        jpaRepository.updateMaxNote(id, maxNote);
        return jpaRepository.findById(id).map(mapper::toDomain).orElseThrow();
    }
}
