package org.dbs.sbgb.infrastructure.persistence.mapper;

import org.dbs.sbgb.domain.model.GalaxyBaseStructure;
import org.dbs.sbgb.infrastructure.persistence.entity.GalaxyBaseStructureEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GalaxyBaseStructureMapper {

    @Mapping(target = "configHash", expression = "java(domain.configHash())")
    GalaxyBaseStructureEntity toEntity(GalaxyBaseStructure domain);

    GalaxyBaseStructure toDomain(GalaxyBaseStructureEntity entity);
}
