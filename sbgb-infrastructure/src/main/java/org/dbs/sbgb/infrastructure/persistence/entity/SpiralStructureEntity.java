package org.dbs.sbgb.infrastructure.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpiralStructureEntity {
    private Integer numberOfArms;
    private Double armWidth;
    private Double armRotation;
    private Double coreSize;
    private Double galaxyRadius;
    private Double darkLaneOpacity;
}
