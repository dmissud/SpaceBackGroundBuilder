package org.dbs.sbgb.domain.model.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EllipticalStructure {
    private Double sersicIndex;
    private Double axisRatio;
    private Double orientationAngle;
}
