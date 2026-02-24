package org.dbs.sbgb.exposition.resources.mapper;

import org.dbs.sbgb.domain.model.NoiseImage;
import org.dbs.sbgb.exposition.resources.dto.NoiseImageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MapperNoiseImage {
    @Mapping(target = "_links", ignore = true)
    NoiseImageDTO toDTO(NoiseImage noiseImage);
}
