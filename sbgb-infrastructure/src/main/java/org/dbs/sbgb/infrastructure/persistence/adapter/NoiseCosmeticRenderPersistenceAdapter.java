package org.dbs.sbgb.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.dbs.sbgb.domain.model.NoiseCosmeticRender;
import org.dbs.sbgb.infrastructure.persistence.jpa.NoiseCosmeticRenderJpaRepository;
import org.dbs.sbgb.infrastructure.persistence.mapper.NoiseCosmeticRenderMapper;
import org.dbs.sbgb.port.out.NoiseCosmeticRenderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NoiseCosmeticRenderPersistenceAdapter implements NoiseCosmeticRenderRepository {

    private final NoiseCosmeticRenderJpaRepository jpaRepository;
    private final NoiseCosmeticRenderMapper mapper;

    @Override
    @Transactional
    public NoiseCosmeticRender save(NoiseCosmeticRender render) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(render)));
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NoiseCosmeticRender> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NoiseCosmeticRender> findByBaseStructureIdAndCosmeticHash(UUID baseStructureId, int cosmeticHash) {
        return jpaRepository.findByBaseStructureIdAndCosmeticHash(baseStructureId, cosmeticHash).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoiseCosmeticRender> findAllByBaseStructureId(UUID baseStructureId) {
        return jpaRepository.findAllByBaseStructureId(baseStructureId).stream().map(mapper::toDomain).toList();
    }
}
