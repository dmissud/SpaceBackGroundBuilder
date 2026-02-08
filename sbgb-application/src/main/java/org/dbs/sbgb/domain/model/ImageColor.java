package org.dbs.sbgb.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class ImageColor {
    private String back;
    private String middle;
    private String fore;
    private double backThreshold;
    private double middleThreshold;
    @Enumerated(EnumType.STRING)
    private InterpolationType interpolationType;

}