package org.dbs.sbgb.domain.model.vo;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RingStructure {
    private Double ringRadius;
    private Double ringWidth;
    private Double ringIntensity;
    private Double coreToRingRatio;
}
