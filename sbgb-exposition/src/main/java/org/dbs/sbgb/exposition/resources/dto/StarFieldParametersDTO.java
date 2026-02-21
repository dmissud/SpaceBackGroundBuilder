package org.dbs.sbgb.exposition.resources.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StarFieldParametersDTO {
    private boolean enabled;
    private double density;
    private int maxStarSize;
    private boolean diffractionSpikes;
    private int spikeCount;
}
