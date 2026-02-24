package org.dbs.sbgb.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "noise_cosmetic_render",
        uniqueConstraints = @UniqueConstraint(columnNames = {"base_structure_id", "cosmetic_hash"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoiseCosmeticRenderEntity {
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

    @Column(name = "back_color", nullable = false, length = 7)
    private String backColor;

    @Column(name = "middle_color", nullable = false, length = 7)
    private String middleColor;

    @Column(name = "fore_color", nullable = false, length = 7)
    private String foreColor;

    @Column(name = "back_threshold", nullable = false)
    private double backThreshold;

    @Column(name = "middle_threshold", nullable = false)
    private double middleThreshold;

    @Column(name = "interpolation_type", nullable = false, length = 50)
    private String interpolationType;

    @Column(name = "transparent_background", nullable = false)
    private boolean transparentBackground;

    @Column(name = "cosmetic_hash", nullable = false)
    private int cosmeticHash;
}
