package org.dbs.sbgb.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "noise_base_structure")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoiseBaseStructureEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "max_note", nullable = false)
    private int maxNote;

    @Column(nullable = false)
    private int width;

    @Column(nullable = false)
    private int height;

    @Column(nullable = false)
    private int seed;

    @Column(nullable = false)
    private int octaves;

    @Column(nullable = false)
    private double persistence;

    @Column(nullable = false)
    private double lacunarity;

    @Column(nullable = false)
    private double scale;

    @Column(name = "noise_type", nullable = false, length = 50)
    private String noiseType;

    @Column(name = "use_multi_layer", nullable = false)
    private boolean useMultiLayer;

    @Column(name = "layers_config", columnDefinition = "TEXT")
    private String layersConfig;

    @Column(name = "config_hash", nullable = false, unique = true)
    private int configHash;
}
