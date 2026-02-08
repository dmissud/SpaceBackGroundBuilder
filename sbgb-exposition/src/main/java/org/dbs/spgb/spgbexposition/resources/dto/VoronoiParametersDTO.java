package org.dbs.spgb.spgbexposition.resources.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoronoiParametersDTO {
    private Integer clusterCount;
    private Double clusterSize;
    private Double clusterConcentration;
}
