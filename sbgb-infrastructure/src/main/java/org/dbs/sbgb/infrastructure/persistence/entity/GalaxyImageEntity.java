package org.dbs.sbgb.infrastructure.persistence.entity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "galaxy_image")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GalaxyImageEntity {
    @Id
    private UUID id;
    private String name;
    private String description;
    private int note;

    @Embedded
    private GalaxyStructureEntity galaxyStructure;

    @Lob
    private byte[] image;
}
