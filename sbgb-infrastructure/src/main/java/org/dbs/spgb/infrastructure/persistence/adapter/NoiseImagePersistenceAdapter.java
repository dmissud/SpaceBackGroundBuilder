package org.dbs.spgb.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.dbs.spgb.domain.model.NoiseImage;
import org.dbs.spgb.infrastructure.persistence.jpa.NoiseImageJpaRepository;
import org.dbs.spgb.port.out.NoiseImageRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NoiseImagePersistenceAdapter implements NoiseImageRepository {

    private final NoiseImageJpaRepository noiseImageJpaRepository;

    @Override
    @Transactional
    public NoiseImage save(NoiseImage noiseImage) {
        return noiseImageJpaRepository.save(noiseImage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoiseImage> findAll() {
        return noiseImageJpaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NoiseImage> findByName(String name) {
        return noiseImageJpaRepository.findByName(name);
    }
}
