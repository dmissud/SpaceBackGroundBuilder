package org.dbs.sbgb.domain.model.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StarField {
    private boolean starFieldEnabled;
    private double starDensity;
    private int maxStarSize;
    private boolean diffractionSpikes;
    private int spikeCount;
}
