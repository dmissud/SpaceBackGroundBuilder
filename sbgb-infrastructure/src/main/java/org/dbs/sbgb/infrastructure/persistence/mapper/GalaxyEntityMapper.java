package org.dbs.sbgb.infrastructure.persistence.mapper;

import org.dbs.sbgb.domain.model.GalaxyImage;
import org.dbs.sbgb.infrastructure.persistence.entity.GalaxyImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GalaxyEntityMapper {

    @Mapping(source = "galaxyStructure.bloomConfig", target = "galaxyStructure.bloom")
    GalaxyImageEntity toEntity(GalaxyImage domainModel);

    @Mapping(source = "galaxyStructure.bloom", target = "galaxyStructure.bloomConfig")
    GalaxyImage toDomain(GalaxyImageEntity entity);
}
