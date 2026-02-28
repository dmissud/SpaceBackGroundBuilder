package org.dbs.sbgb.exposition.resources.mapper;

import org.dbs.sbgb.domain.model.GalaxyCosmeticRender;
import org.dbs.sbgb.exposition.resources.dto.GalaxyCosmeticRenderDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", implementationName = "GalaxyCosmeticRenderDTOMapperImpl")
public interface GalaxyCosmeticRenderDTOMapper {

    GalaxyCosmeticRenderDTO toDTO(GalaxyCosmeticRender domain);
}
