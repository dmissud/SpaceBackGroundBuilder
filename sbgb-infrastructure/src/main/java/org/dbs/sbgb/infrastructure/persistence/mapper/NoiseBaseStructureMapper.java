package org.dbs.sbgb.infrastructure.persistence.mapper;

import org.dbs.sbgb.domain.model.NoiseBaseStructure;
import org.dbs.sbgb.infrastructure.persistence.entity.NoiseBaseStructureEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NoiseBaseStructureMapper {

    @Mapping(target = "configHash", expression = "java(domain.configHash())")
    NoiseBaseStructureEntity toEntity(NoiseBaseStructure domain);

    NoiseBaseStructure toDomain(NoiseBaseStructureEntity entity);
}
