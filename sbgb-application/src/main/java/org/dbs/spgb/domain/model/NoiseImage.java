package org.dbs.spgb.domain.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class NoiseImage {
    @Id
    private UUID id;
    private String name;
    private String description;
    private int note;
    @Embedded
    private ImageStructure imageStructure;
    @Embedded
    private ImageColor imageColor;

    @Lob
    private byte[] image;
}