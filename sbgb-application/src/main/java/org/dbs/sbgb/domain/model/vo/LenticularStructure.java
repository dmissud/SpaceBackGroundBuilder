package org.dbs.sbgb.domain.model.vo;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LenticularStructure {
    Double sersicIndex;
    Double axisRatio;
    Double orientationAngle;
    Double diskContribution;
}
