package org.dbs.sbgb.domain.model.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpiralStructure {
    private Integer numberOfArms;
    private Double armWidth;
    private Double armRotation;
    private Double coreSize;
    private Double galaxyRadius;
    private Double darkLaneOpacity;
}
