package org.dbs.sbgb.infrastructure.persistence.mapper;

import org.dbs.sbgb.domain.model.NoiseCosmeticRender;
import org.dbs.sbgb.infrastructure.persistence.entity.NoiseCosmeticRenderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NoiseCosmeticRenderMapper {

    @Mapping(target = "backColor", source = "back")
    @Mapping(target = "middleColor", source = "middle")
    @Mapping(target = "foreColor", source = "fore")
    @Mapping(target = "cosmeticHash", expression = "java(domain.cosmeticHash())")
    NoiseCosmeticRenderEntity toEntity(NoiseCosmeticRender domain);

    @Mapping(target = "back", source = "backColor")
    @Mapping(target = "middle", source = "middleColor")
    @Mapping(target = "fore", source = "foreColor")
    NoiseCosmeticRender toDomain(NoiseCosmeticRenderEntity entity);
}
