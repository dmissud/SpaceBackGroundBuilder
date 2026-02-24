package org.dbs.sbgb.exposition.resources.mapper;

import org.dbs.sbgb.domain.model.NoiseBaseStructure;
import org.dbs.sbgb.exposition.resources.dto.NoiseBaseStructureDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", implementationName = "NoiseBaseStructureDTOMapperImpl")
public interface NoiseBaseStructureMapper {
    NoiseBaseStructureDTO toDTO(NoiseBaseStructure domain);
}
