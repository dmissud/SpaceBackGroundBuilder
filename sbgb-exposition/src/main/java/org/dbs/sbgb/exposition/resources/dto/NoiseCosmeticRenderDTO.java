package org.dbs.sbgb.exposition.resources.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class NoiseCosmeticRenderDTO {
    private UUID id;
    private UUID baseStructureId;
    private String description;
    private int note;
    private String back;
    private String middle;
    private String fore;
    private double backThreshold;
    private double middleThreshold;
    private String interpolationType;
    private boolean transparentBackground;
}
