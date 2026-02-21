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
public class VoronoiCluster {
    private Integer clusterCount;
    private Double clusterSize;
    private Double clusterConcentration;
}
