package org.dbs.sbgb.infrastructure.persistence.mapper;

import org.dbs.sbgb.domain.model.GalaxyImage;
import org.dbs.sbgb.infrastructure.persistence.entity.GalaxyImageEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GalaxyEntityMapper {
    GalaxyImageEntity toEntity(GalaxyImage domainModel);

    GalaxyImage toDomain(GalaxyImageEntity entity);
}
