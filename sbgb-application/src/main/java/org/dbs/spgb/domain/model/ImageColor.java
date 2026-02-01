package org.dbs.spgb.domain.model;

import jakarta.persistence.Embeddable;
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

}