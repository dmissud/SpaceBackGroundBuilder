package org.dbs.sbgb.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "galaxy_base_structure")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GalaxyBaseStructureEntity {
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
    private long seed;

    @Column(name = "galaxy_type", nullable = false, length = 50)
    private String galaxyType;

    @Column(name = "core_size", nullable = false)
    private double coreSize;

    @Column(name = "galaxy_radius", nullable = false)
    private double galaxyRadius;

    @Column(name = "warp_strength", nullable = false)
    private double warpStrength;

    @Column(name = "noise_octaves", nullable = false)
    private int noiseOctaves;

    @Column(name = "noise_persistence", nullable = false)
    private double noisePersistence;

    @Column(name = "noise_lacunarity", nullable = false)
    private double noiseLacunarity;

    @Column(name = "noise_scale", nullable = false)
    private double noiseScale;

    @Column(name = "multi_layer_enabled", nullable = false)
    private boolean multiLayerEnabled;

    @Column(name = "macro_layer_scale", nullable = false)
    private double macroLayerScale;

    @Column(name = "macro_layer_weight", nullable = false)
    private double macroLayerWeight;

    @Column(name = "meso_layer_scale", nullable = false)
    private double mesoLayerScale;

    @Column(name = "meso_layer_weight", nullable = false)
    private double mesoLayerWeight;

    @Column(name = "micro_layer_scale", nullable = false)
    private double microLayerScale;

    @Column(name = "micro_layer_weight", nullable = false)
    private double microLayerWeight;

    @Column(name = "structure_params", columnDefinition = "TEXT")
    private String structureParams;

    @Column(name = "config_hash", nullable = false, unique = true)
    private int configHash;
}
