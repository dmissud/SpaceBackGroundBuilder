package org.dbs.sbgb.infrastructure.persistence.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dbs.sbgb.domain.model.InterpolationType;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageColorEntity {
    private String back;
    private String middle;
    private String fore;
    private double backThreshold;
    private double middleThreshold;
    @Enumerated(EnumType.STRING)
    private InterpolationType interpolationType;
}
