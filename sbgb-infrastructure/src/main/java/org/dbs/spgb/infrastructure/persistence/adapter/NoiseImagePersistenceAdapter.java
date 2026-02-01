package org.dbs.spgb.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.dbs.spgb.domain.model.NoiseImage;
import org.dbs.spgb.infrastructure.persistence.jpa.NoiseImageJpaRepository;
import org.dbs.spgb.port.out.NoiseImageRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoiseImagePersistenceAdapter implements NoiseImageRepository {

    private final NoiseImageJpaRepository noiseImageJpaRepository;

    @Override
    public NoiseImage save(NoiseImage noiseImage) {
        return noiseImageJpaRepository.save(noiseImage);
    }
}
