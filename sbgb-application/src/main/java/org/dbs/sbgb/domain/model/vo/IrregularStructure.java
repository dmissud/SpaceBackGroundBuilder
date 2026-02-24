package org.dbs.sbgb.domain.model.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IrregularStructure {
    private Double irregularity;
    private Integer irregularClumpCount;
    private Double irregularClumpSize;
}
