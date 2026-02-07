package org.dbs.spgb.spgbexposition.resources.mapper;

import org.dbs.spgb.domain.model.GalaxyImage;
import org.dbs.spgb.spgbexposition.resources.dto.GalaxyImageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MapperGalaxyImage {
    GalaxyImageDTO toDTO(GalaxyImage galaxyImage);
}
