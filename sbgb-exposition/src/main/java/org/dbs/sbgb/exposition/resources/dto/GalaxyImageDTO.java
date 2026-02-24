package org.dbs.sbgb.exposition.resources.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GalaxyImageDTO extends RepresentationModel<GalaxyImageDTO> {
    private UUID id;
    private String description;
    private int note;
    private GalaxyStructureDTO galaxyStructure;
}
