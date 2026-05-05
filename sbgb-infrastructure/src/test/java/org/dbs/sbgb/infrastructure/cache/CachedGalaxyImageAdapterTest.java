package org.dbs.sbgb.infrastructure.cache;

import org.dbs.sbgb.domain.factory.NoiseGeneratorFactory;
import org.dbs.sbgb.domain.mapper.GalaxyStructureMapper;
import org.dbs.sbgb.domain.model.GalaxyColorCalculator;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.service.BloomApplicator;
import org.dbs.sbgb.domain.service.StarFieldApplicator;
import org.dbs.sbgb.domain.strategy.GalaxyGeneratorFactory;
import org.dbs.sbgb.port.in.GalaxyRequestCmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CachedGalaxyImageAdapterTest {

    @Mock private GalaxyStructureMapper galaxyStructureMapper;
    @Mock private GalaxyGeneratorFactory galaxyGeneratorFactory;
    @Mock private NoiseGeneratorFactory noiseGeneratorFactory;
    @Mock private StarFieldApplicator starFieldApplicator;
    @Mock private BloomApplicator bloomApplicator;
    @Mock private GalaxyParameters galaxyParameters;
    @Mock private GalaxyColorCalculator colorCalculator;
    @Mock private org.dbs.sbgb.domain.model.GalaxyIntensityCalculator intensityCalculator;

    private CachedGalaxyImageAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CachedGalaxyImageAdapter(
                galaxyStructureMapper, galaxyGeneratorFactory,
                noiseGeneratorFactory, starFieldApplicator, bloomApplicator
        );
    }

    @Test
    void shouldComputeImage() {
        GalaxyRequestCmd cmd = GalaxyRequestCmd.builder()
                .width(10).height(10).seed(123L).build();
        
        when(galaxyParameters.getGalaxyType()).thenReturn(org.dbs.sbgb.domain.model.GalaxyType.SPIRAL);
        when(galaxyParameters.getDomainWarpParameters()).thenReturn(mock(org.dbs.sbgb.domain.model.parameters.DomainWarpParameters.class));
        when(galaxyParameters.getMultiLayerNoiseParameters()).thenReturn(mock(org.dbs.sbgb.domain.model.parameters.MultiLayerNoiseParameters.class));
        
        when(galaxyStructureMapper.toGalaxyParameters(cmd)).thenReturn(galaxyParameters);
        when(galaxyStructureMapper.createColorCalculator(any())).thenReturn(colorCalculator);
        
        when(colorCalculator.getSpaceBackgroundColor()).thenReturn(Color.BLACK);
        when(colorCalculator.calculateGalaxyColor(anyDouble())).thenReturn(Color.WHITE);
        
        when(galaxyGeneratorFactory.create(any(), any())).thenReturn(intensityCalculator);
        when(intensityCalculator.calculateGalaxyIntensity(anyInt(), anyInt())).thenReturn(0.5);

        when(starFieldApplicator.applyIfEnabled(any(), any(), anyLong())).thenAnswer(i -> i.getArgument(0));
        when(bloomApplicator.applyIfEnabled(any(), any())).thenAnswer(i -> i.getArgument(0));

        BufferedImage result = adapter.computeImage(12345, cmd);
        
        assertThat(result).isNotNull();
        assertThat(result.getWidth()).isEqualTo(10);
        assertThat(result.getHeight()).isEqualTo(10);
    }
}
