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
public class EllipticalStructureEntity {
    private Double sersicIndex;
    private Double axisRatio;
    private Double orientationAngle;
}
