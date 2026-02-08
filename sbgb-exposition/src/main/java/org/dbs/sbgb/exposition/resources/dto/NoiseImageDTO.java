package org.dbs.sbgb.exposition.resources.dto;

import lombok.Getter;
import lombok.Setter;
import org.dbs.sbgb.domain.model.ImageColor;
import org.dbs.sbgb.domain.model.ImageStructure;
import org.springframework.hateoas.Link;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class NoiseImageDTO {
    private UUID id;
    private String name;
    private String description;
    private int note;
    private ImageStructure imageStructure;
    private ImageColor imageColor;
    private Map<String, Link> _links = new HashMap<>();
}
