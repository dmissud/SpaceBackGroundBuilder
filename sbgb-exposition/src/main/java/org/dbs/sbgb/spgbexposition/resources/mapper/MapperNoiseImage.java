package org.dbs.sbgb.spgbexposition.resources.mapper;

import org.dbs.sbgb.domain.model.NoiseImage;
import org.dbs.sbgb.spgbexposition.resources.dto.NoiseImageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MapperNoiseImage {
    @Mapping(target = "_links", ignore = true)
    NoiseImageDTO toDTO(NoiseImage noiseImage);
}
