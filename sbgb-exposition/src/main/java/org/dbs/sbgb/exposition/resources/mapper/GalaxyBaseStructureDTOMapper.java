package org.dbs.sbgb.exposition.resources.mapper;

import org.dbs.sbgb.domain.model.GalaxyBaseStructure;
import org.dbs.sbgb.exposition.resources.dto.GalaxyBaseStructureDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", implementationName = "GalaxyBaseStructureDTOMapperImpl")
public interface GalaxyBaseStructureDTOMapper {

    GalaxyBaseStructureDTO toDTO(GalaxyBaseStructure domain);
}
