package org.dbs.sbgb.exposition.resources.mapper;

import org.dbs.sbgb.domain.model.NoiseCosmeticRender;
import org.dbs.sbgb.exposition.resources.dto.NoiseCosmeticRenderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", implementationName = "NoiseCosmeticRenderDTOMapperImpl")
public interface NoiseCosmeticRenderMapper {

    NoiseCosmeticRenderDTO toDTO(NoiseCosmeticRender domain);
}
