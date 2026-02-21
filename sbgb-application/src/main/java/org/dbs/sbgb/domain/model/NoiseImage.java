package org.dbs.sbgb.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NoiseImage {
    private UUID id;
    private String name;
    private String description;
    private int note;
    private ImageStructure imageStructure;
    private ImageColor imageColor;

    private byte[] image;
}