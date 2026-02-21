package org.dbs.sbgb.infrastructure.persistence.mapper;

import org.dbs.sbgb.domain.model.NoiseImage;
import org.dbs.sbgb.infrastructure.persistence.entity.NoiseImageEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NoiseEntityMapper {
    NoiseImageEntity toEntity(NoiseImage domainModel);

    NoiseImage toDomain(NoiseImageEntity entity);
}
