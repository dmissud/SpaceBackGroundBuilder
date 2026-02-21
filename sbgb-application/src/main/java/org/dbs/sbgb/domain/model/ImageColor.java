package org.dbs.sbgb.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ImageColor {
    private String back;
    private String middle;
    private String fore;
    private double backThreshold;
    private double middleThreshold;
    private InterpolationType interpolationType;

}