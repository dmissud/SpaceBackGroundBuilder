package org.dbs.spgb.spgbexposition.resources.mapper;

import org.dbs.spgb.domain.model.NoiseImage;
import org.dbs.spgb.spgbexposition.resources.dto.NoiseImageDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapperNoiseImage {
    NoiseImageDTO toDTO(NoiseImage noiseImage);
}
