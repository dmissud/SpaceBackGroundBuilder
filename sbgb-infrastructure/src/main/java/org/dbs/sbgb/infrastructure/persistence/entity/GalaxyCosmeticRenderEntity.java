package org.dbs.sbgb.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "galaxy_cosmetic_render",
        uniqueConstraints = @UniqueConstraint(columnNames = {"base_structure_id", "cosmetic_hash"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GalaxyCosmeticRenderEntity {
    @Id
    private UUID id;

    @Column(name = "base_structure_id", nullable = false)
    private UUID baseStructureId;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private int note;

    @Lob
    private byte[] thumbnail;

    @Column(name = "color_palette", length = 50)
    private String colorPalette;

    @Column(name = "space_background_color", length = 7)
    private String spaceBackgroundColor;

    @Column(name = "core_color", length = 7)
    private String coreColor;

    @Column(name = "arm_color", length = 7)
    private String armColor;

    @Column(name = "outer_color", length = 7)
    private String outerColor;

    @Column(name = "bloom_enabled", nullable = false)
    private boolean bloomEnabled;

    @Column(name = "bloom_radius", nullable = false)
    private double bloomRadius;

    @Column(name = "bloom_intensity", nullable = false)
    private double bloomIntensity;

    @Column(name = "bloom_threshold", nullable = false)
    private double bloomThreshold;

    @Column(name = "star_field_enabled", nullable = false)
    private boolean starFieldEnabled;

    @Column(name = "star_density", nullable = false)
    private double starDensity;

    @Column(name = "max_star_size", nullable = false)
    private double maxStarSize;

    @Column(name = "diffraction_spikes", nullable = false)
    private boolean diffractionSpikes;

    @Column(name = "spike_count", nullable = false)
    private int spikeCount;

    @Column(name = "cosmetic_hash", nullable = false)
    private int cosmeticHash;
}
