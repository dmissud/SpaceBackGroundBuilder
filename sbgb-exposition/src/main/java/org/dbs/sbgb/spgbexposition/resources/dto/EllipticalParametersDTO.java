package org.dbs.sbgb.spgbexposition.resources.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EllipticalParametersDTO {
    private Double sersicIndex;
    private Double axisRatio;
    private Double orientationAngle;
}
