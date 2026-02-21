package org.dbs.sbgb.domain.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BloomConfig {
    private boolean bloomEnabled;
    private int bloomRadius;
    private double bloomIntensity;
    private double bloomThreshold;
}
