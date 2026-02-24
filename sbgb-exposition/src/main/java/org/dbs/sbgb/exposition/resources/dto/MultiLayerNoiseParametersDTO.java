package org.dbs.sbgb.exposition.resources.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MultiLayerNoiseParametersDTO {
    private boolean enabled;
    private double macroLayerScale;
    private double macroLayerWeight;
    private double mesoLayerScale;
    private double mesoLayerWeight;
    private double microLayerScale;
    private double microLayerWeight;
}
