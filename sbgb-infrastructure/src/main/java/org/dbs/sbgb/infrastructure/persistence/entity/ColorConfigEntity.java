package org.dbs.sbgb.infrastructure.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColorConfigEntity {
    private String colorPalette;
    private String spaceBackgroundColor;
    private String coreColor;
    private String armColor;
    private String outerColor;
}
