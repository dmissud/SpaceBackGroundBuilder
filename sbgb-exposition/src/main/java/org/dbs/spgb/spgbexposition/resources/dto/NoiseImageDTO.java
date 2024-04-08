package org.dbs.spgb.spgbexposition.resources.dto;

import lombok.Getter;
import org.springframework.hateoas.Link;

import java.util.Map;

@Getter
public class NoiseImageDTO {
    private Map<String, Link> _links;
}
