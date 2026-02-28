package org.dbs.sbgb.infrastructure.persistence.mapper;

import org.dbs.sbgb.domain.model.GalaxyCosmeticRender;
import org.dbs.sbgb.infrastructure.persistence.entity.GalaxyCosmeticRenderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GalaxyCosmeticRenderMapper {

    @Mapping(target = "cosmeticHash", expression = "java(domain.cosmeticHash())")
    GalaxyCosmeticRenderEntity toEntity(GalaxyCosmeticRender domain);

    GalaxyCosmeticRender toDomain(GalaxyCosmeticRenderEntity entity);
}
