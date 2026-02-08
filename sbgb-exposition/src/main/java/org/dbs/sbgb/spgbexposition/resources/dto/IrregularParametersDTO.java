package org.dbs.sbgb.spgbexposition.resources.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IrregularParametersDTO {
    private Double irregularity;
    private Integer irregularClumpCount;
    private Double irregularClumpSize;
}
