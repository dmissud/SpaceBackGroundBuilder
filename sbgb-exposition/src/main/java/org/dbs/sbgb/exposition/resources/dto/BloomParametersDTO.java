package org.dbs.sbgb.exposition.resources.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BloomParametersDTO {
    private boolean enabled;
    private int bloomRadius;
    private double bloomIntensity;
    private double bloomThreshold;
}
