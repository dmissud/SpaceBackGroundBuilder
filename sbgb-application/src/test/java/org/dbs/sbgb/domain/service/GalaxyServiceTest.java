package org.dbs.sbgb.domain.service;

import org.dbs.sbgb.domain.factory.NoiseGeneratorFactory;
import org.dbs.sbgb.domain.mapper.GalaxyStructureMapper;
import org.dbs.sbgb.domain.model.*;
import org.dbs.sbgb.domain.strategy.GalaxyGeneratorFactory;
import org.dbs.sbgb.port.in.GalaxyRequestCmd;
import org.dbs.sbgb.port.out.GalaxyBaseStructureRepository;
import org.dbs.sbgb.port.out.GalaxyCosmeticRenderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GalaxyServiceTest {

    @Mock private GalaxyBaseStructureRepository baseStructureRepository;
    @Mock private GalaxyCosmeticRenderRepository cosmeticRenderRepository;
    @Mock private GalaxyStructureMapper galaxyStructureMapper;
    @Mock private GalaxyGeneratorFactory galaxyGeneratorFactory;
    @Mock private NoiseGeneratorFactory noiseGeneratorFactory;
    @Mock private StarFieldApplicator starFieldApplicator;
    @Mock private BloomApplicator bloomApplicator;
    @Mock private ImageSerializer imageSerializer;

    private GalaxyService galaxyService;

    @BeforeEach
    void setUp() {
        org.mockito.MockitoAnnotations.openMocks(this);
        galaxyService = new GalaxyService(baseStructureRepository, cosmeticRenderRepository,
                galaxyStructureMapper, galaxyGeneratorFactory, noiseGeneratorFactory,
                starFieldApplicator, bloomApplicator, imageSerializer);
    }

    @Test
    void shouldBuildGalaxyImage() throws IOException {
        GalaxyRequestCmd cmd = GalaxyRequestCmd.builder()
                .width(100).height(100).build();

        GalaxyParameters params = mock(GalaxyParameters.class);
        when(params.getGalaxyType()).thenReturn(GalaxyType.SPIRAL);
        when(params.getMultiLayerNoiseParameters()).thenReturn(
                mock(org.dbs.sbgb.domain.model.parameters.MultiLayerNoiseParameters.class));
        when(params.getDomainWarpParameters()).thenReturn(
                mock(org.dbs.sbgb.domain.model.parameters.DomainWarpParameters.class));
        when(params.getStarFieldParameters()).thenReturn(
                mock(org.dbs.sbgb.domain.model.parameters.StarFieldParameters.class));
        when(galaxyStructureMapper.toGalaxyParameters(cmd)).thenReturn(params);

        GalaxyColorCalculator colorCalculator = mock(GalaxyColorCalculator.class);
        when(colorCalculator.getSpaceBackgroundColor()).thenReturn(Color.BLACK);
        when(colorCalculator.calculateGalaxyColor(anyDouble())).thenReturn(Color.WHITE);
        when(galaxyStructureMapper.createColorCalculator(any())).thenReturn(colorCalculator);

        PerlinGenerator noiseGenerator = mock(PerlinGenerator.class);
        when(noiseGeneratorFactory.createNoiseGenerator(any(), anyLong(), anyInt(), anyInt(), any(), any()))
                .thenReturn(noiseGenerator);

        GalaxyIntensityCalculator intensityCalculator = mock(GalaxyIntensityCalculator.class);
        when(intensityCalculator.calculateGalaxyIntensity(anyInt(), anyInt())).thenReturn(0.5);
        when(galaxyGeneratorFactory.create(any(GalaxyType.class), any())).thenReturn(intensityCalculator);

        when(starFieldApplicator.applyIfEnabled(any(), any(), anyLong())).thenAnswer(i -> i.getArgument(0));
        when(bloomApplicator.applyIfEnabled(any(), any())).thenAnswer(i -> i.getArgument(0));

        byte[] fakeBytes = new byte[]{10, 11, 12};
        when(imageSerializer.toByteArray(any(BufferedImage.class))).thenReturn(fakeBytes);

        byte[] result = galaxyService.buildGalaxyImage(cmd);

        assertThat(result).isEqualTo(fakeBytes);
        verify(imageSerializer).toByteArray(any(BufferedImage.class));
    }
}
