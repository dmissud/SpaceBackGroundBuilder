package org.dbs.sbgb.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dbs.sbgb.domain.model.vo.BloomConfig;
import org.dbs.sbgb.domain.model.vo.ColorConfig;
import org.dbs.sbgb.domain.model.vo.EllipticalStructure;
import org.dbs.sbgb.domain.model.vo.IrregularStructure;
import org.dbs.sbgb.domain.model.vo.MultiLayerNoise;
import org.dbs.sbgb.domain.model.vo.NoiseTexture;
import org.dbs.sbgb.domain.model.vo.RingStructure;
import org.dbs.sbgb.domain.model.vo.SpiralStructure;
import org.dbs.sbgb.domain.model.vo.StarField;
import org.dbs.sbgb.domain.model.vo.VoronoiCluster;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class GalaxyStructure {

    private int width;
    private int height;
    private long seed;
    private String galaxyType;
    private double warpStrength;

    private SpiralStructure spiralStructure;

    private NoiseTexture noiseTexture;

    private VoronoiCluster voronoiCluster;

    private EllipticalStructure ellipticalStructure;

    private RingStructure ringStructure;

    private IrregularStructure irregularStructure;

    private StarField starField;

    private MultiLayerNoise multiLayerNoise;

    private BloomConfig bloomConfig;

    private ColorConfig colorConfig;

}
