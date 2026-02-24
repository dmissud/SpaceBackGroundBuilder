package org.dbs.sbgb.domain.model.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiLayerNoise {
    private boolean multiLayerNoiseEnabled;
    private double macroLayerScale;
    private double macroLayerWeight;
    private double mesoLayerScale;
    private double mesoLayerWeight;
    private double microLayerScale;
    private double microLayerWeight;
}
