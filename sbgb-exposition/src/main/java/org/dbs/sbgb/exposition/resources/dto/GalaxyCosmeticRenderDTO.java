package org.dbs.sbgb.exposition.resources.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GalaxyCosmeticRenderDTO {
    private UUID id;
    private UUID baseStructureId;
    private String description;
    private int note;
    private byte[] thumbnail;
    private String colorPalette;
    private String spaceBackgroundColor;
    private String coreColor;
    private String armColor;
    private String outerColor;
    private boolean bloomEnabled;
    private double bloomRadius;
    private double bloomIntensity;
    private double bloomThreshold;
    private boolean starFieldEnabled;
    private double starDensity;
    private double maxStarSize;
    private boolean diffractionSpikes;
    private int spikeCount;
}
