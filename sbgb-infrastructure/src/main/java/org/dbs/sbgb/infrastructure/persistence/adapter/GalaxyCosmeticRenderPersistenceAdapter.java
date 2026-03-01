package org.dbs.sbgb.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.dbs.sbgb.domain.model.GalaxyCosmeticRender;
import org.dbs.sbgb.infrastructure.persistence.jpa.GalaxyCosmeticRenderJpaRepository;
import org.dbs.sbgb.infrastructure.persistence.mapper.GalaxyCosmeticRenderMapper;
import org.dbs.sbgb.port.out.GalaxyCosmeticRenderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GalaxyCosmeticRenderPersistenceAdapter implements GalaxyCosmeticRenderRepository {

    private final GalaxyCosmeticRenderJpaRepository jpaRepository;
    private final GalaxyCosmeticRenderMapper mapper;

    @Override
    @Transactional
    public GalaxyCosmeticRender save(GalaxyCosmeticRender render) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(render)));
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GalaxyCosmeticRender> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GalaxyCosmeticRender> findByBaseStructureIdAndCosmeticHash(UUID baseId, int cosmeticHash) {
        return jpaRepository.findByBaseStructureIdAndCosmeticHash(baseId, cosmeticHash).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GalaxyCosmeticRender> findAllByBaseStructureId(UUID baseId) {
        return jpaRepository.findAllByBaseStructureId(baseId).stream().map(mapper::toDomain).toList();
    }
}
