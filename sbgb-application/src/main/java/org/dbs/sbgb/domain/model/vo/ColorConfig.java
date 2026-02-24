package org.dbs.sbgb.domain.model.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColorConfig {
    private String colorPalette;

    // Legacy color properties
    private String spaceBackgroundColor;
    private String coreColor;
    private String armColor;
    private String outerColor;
}
