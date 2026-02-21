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
public class ColorConfig {
    private String colorPalette;

    // Legacy color properties
    private String spaceBackgroundColor;
    private String coreColor;
    private String armColor;
    private String outerColor;
}
