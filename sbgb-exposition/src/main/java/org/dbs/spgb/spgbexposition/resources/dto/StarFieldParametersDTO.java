package org.dbs.spgb.spgbexposition.resources.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StarFieldParametersDTO {
    private double density;
    private int maxStarSize;
    private boolean diffractionSpikes;
    private int spikeCount;
}
