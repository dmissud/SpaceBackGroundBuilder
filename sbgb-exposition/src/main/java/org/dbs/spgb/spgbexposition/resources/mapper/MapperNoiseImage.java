package org.dbs.spgb.spgbexposition.resources.mapper;

import org.dbs.spgb.domain.model.NoiseImage;
import org.dbs.spgb.spgbexposition.resources.dto.NoiseImageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MapperNoiseImage {
    @Mapping(target = "_links", ignore = true)
    NoiseImageDTO toDTO(NoiseImage noiseImage);
}
