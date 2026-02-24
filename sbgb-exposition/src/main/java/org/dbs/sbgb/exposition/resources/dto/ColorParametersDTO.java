package org.dbs.sbgb.exposition.resources.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ColorParametersDTO {
    private String colorPalette;
    private String spaceBackgroundColor;
    private String coreColor;
    private String armColor;
    private String outerColor;
}
