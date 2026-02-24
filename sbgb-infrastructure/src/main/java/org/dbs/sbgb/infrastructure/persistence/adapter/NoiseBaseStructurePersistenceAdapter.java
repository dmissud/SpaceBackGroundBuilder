package org.dbs.sbgb.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.dbs.sbgb.domain.model.NoiseBaseStructure;
import org.dbs.sbgb.infrastructure.persistence.jpa.NoiseBaseStructureJpaRepository;
import org.dbs.sbgb.infrastructure.persistence.mapper.NoiseBaseStructureMapper;
import org.dbs.sbgb.port.out.NoiseBaseStructureRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NoiseBaseStructurePersistenceAdapter implements NoiseBaseStructureRepository {

    private final NoiseBaseStructureJpaRepository jpaRepository;
    private final NoiseBaseStructureMapper mapper;

    @Override
    @Transactional
    public NoiseBaseStructure save(NoiseBaseStructure structure) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(structure)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoiseBaseStructure> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NoiseBaseStructure> findByConfigHash(int hash) {
        return jpaRepository.findByConfigHash(hash).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public NoiseBaseStructure updateMaxNote(UUID id, int maxNote) {
        jpaRepository.updateMaxNote(id, maxNote);
        return jpaRepository.findById(id).map(mapper::toDomain).orElseThrow();
    }
}
