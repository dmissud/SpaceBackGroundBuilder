package org.dbs.sbgb.infrastructure.persistence.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GalaxyStructureEntity {

        private int width;
        private int height;
        private long seed;
        private String galaxyType;
        private double warpStrength;

        @Embedded
        @AttributeOverrides({
                        @AttributeOverride(name = "numberOfArms", column = @Column(name = "number_of_arms")),
                        @AttributeOverride(name = "armWidth", column = @Column(name = "arm_width")),
                        @AttributeOverride(name = "armRotation", column = @Column(name = "arm_rotation")),
                        @AttributeOverride(name = "coreSize", column = @Column(name = "core_size")),
                        @AttributeOverride(name = "galaxyRadius", column = @Column(name = "galaxy_radius")),
                        @AttributeOverride(name = "darkLaneOpacity", column = @Column(name = "dark_lane_opacity"))
        })
        private SpiralStructureEntity spiralStructure;

        @Embedded
        @AttributeOverrides({
                        @AttributeOverride(name = "noiseOctaves", column = @Column(name = "noise_octaves")),
                        @AttributeOverride(name = "noisePersistence", column = @Column(name = "noise_persistence")),
                        @AttributeOverride(name = "noiseLacunarity", column = @Column(name = "noise_lacunarity")),
                        @AttributeOverride(name = "noiseScale", column = @Column(name = "noise_scale"))
        })
        private NoiseTextureEntity noiseTexture;

        @Embedded
        @AttributeOverrides({
                        @AttributeOverride(name = "clusterCount", column = @Column(name = "cluster_count")),
                        @AttributeOverride(name = "clusterSize", column = @Column(name = "cluster_size")),
                        @AttributeOverride(name = "clusterConcentration", column = @Column(name = "cluster_concentration"))
        })
        private VoronoiClusterEntity voronoiCluster;

        @Embedded
        @AttributeOverrides({
                        @AttributeOverride(name = "sersicIndex", column = @Column(name = "sersic_index")),
                        @AttributeOverride(name = "axisRatio", column = @Column(name = "axis_ratio")),
                        @AttributeOverride(name = "orientationAngle", column = @Column(name = "orientation_angle"))
        })
        private EllipticalStructureEntity ellipticalStructure;

        @Embedded
        @AttributeOverrides({
                        @AttributeOverride(name = "ringRadius", column = @Column(name = "ring_radius")),
                        @AttributeOverride(name = "ringWidth", column = @Column(name = "ring_width")),
                        @AttributeOverride(name = "ringIntensity", column = @Column(name = "ring_intensity")),
                        @AttributeOverride(name = "coreToRingRatio", column = @Column(name = "core_to_ring_ratio"))
        })
        private RingStructureEntity ringStructure;

        @Embedded
        @AttributeOverrides({
                        @AttributeOverride(name = "irregularity", column = @Column(name = "irregularity")),
                        @AttributeOverride(name = "irregularClumpCount", column = @Column(name = "irregular_clump_count")),
                        @AttributeOverride(name = "irregularClumpSize", column = @Column(name = "irregular_clump_size"))
        })
        private IrregularStructureEntity irregularStructure;

        @Embedded
        @AttributeOverrides({
                        @AttributeOverride(name = "starFieldEnabled", column = @Column(name = "star_field_enabled")),
                        @AttributeOverride(name = "starDensity", column = @Column(name = "star_density")),
                        @AttributeOverride(name = "maxStarSize", column = @Column(name = "max_star_size")),
                        @AttributeOverride(name = "diffractionSpikes", column = @Column(name = "diffraction_spikes")),
                        @AttributeOverride(name = "spikeCount", column = @Column(name = "spike_count"))
        })
        private StarFieldEntity starField;

        @Embedded
        @AttributeOverrides({
                        @AttributeOverride(name = "multiLayerNoiseEnabled", column = @Column(name = "multi_layer_noise_enabled")),
                        @AttributeOverride(name = "macroLayerScale", column = @Column(name = "macro_layer_scale")),
                        @AttributeOverride(name = "macroLayerWeight", column = @Column(name = "macro_layer_weight")),
                        @AttributeOverride(name = "mesoLayerScale", column = @Column(name = "meso_layer_scale")),
                        @AttributeOverride(name = "mesoLayerWeight", column = @Column(name = "meso_layer_weight")),
                        @AttributeOverride(name = "microLayerScale", column = @Column(name = "micro_layer_scale")),
                        @AttributeOverride(name = "microLayerWeight", column = @Column(name = "micro_layer_weight"))
        })
        private MultiLayerNoiseEntity multiLayerNoise;

        @Embedded
        @AttributeOverrides({
                        @AttributeOverride(name = "bloomEnabled", column = @Column(name = "bloom_enabled")),
                        @AttributeOverride(name = "bloomRadius", column = @Column(name = "bloom_radius")),
                        @AttributeOverride(name = "bloomIntensity", column = @Column(name = "bloom_intensity")),
                        @AttributeOverride(name = "bloomThreshold", column = @Column(name = "bloom_threshold"))
        })
        private BloomEntity bloom;

        @Embedded
        @AttributeOverrides({
                        @AttributeOverride(name = "colorPalette", column = @Column(name = "color_palette")),
                        @AttributeOverride(name = "spaceBackgroundColor", column = @Column(name = "space_background_color")),
                        @AttributeOverride(name = "coreColor", column = @Column(name = "core_color")),
                        @AttributeOverride(name = "armColor", column = @Column(name = "arm_color")),
                        @AttributeOverride(name = "outerColor", column = @Column(name = "outer_color"))
        })
        private ColorConfigEntity colorConfig;

}
