package org.dbs.sbgb.spgbexposition.resources.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpiralParametersDTO {
    private Integer numberOfArms;
    private Double armWidth;
    private Double armRotation;
}
