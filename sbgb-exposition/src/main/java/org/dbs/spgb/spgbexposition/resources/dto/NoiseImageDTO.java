package org.dbs.spgb.spgbexposition.resources.dto;

import lombok.Getter;
import lombok.Setter;
import org.dbs.spgb.domain.model.ImageColor;
import org.dbs.spgb.domain.model.ImageStructure;
import org.springframework.hateoas.Link;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class NoiseImageDTO {
    private UUID id;
    private String description;
    private int note;
    private ImageStructure imageStructure;
    private ImageColor imageColor;
    private Map<String, Link> _links = new HashMap<>();
}
