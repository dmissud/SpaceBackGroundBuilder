package org.dbs.sbgb.domain.service;

import org.dbs.sbgb.domain.factory.NoiseGeneratorFactory;
import org.dbs.sbgb.domain.mapper.GalaxyStructureMapper;
import org.dbs.sbgb.domain.model.*;
import org.dbs.sbgb.domain.strategy.GalaxyGeneratorFactory;
import org.dbs.sbgb.port.in.GalaxyRequestCmd;
import org.dbs.sbgb.port.out.GalaxyImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class GalaxyServiceTest {

        @Mock
        private GalaxyImageRepository galaxyImageRepository;
        @Mock
        private GalaxyStructureMapper galaxyStructureMapper;
        @Mock
        private GalaxyGeneratorFactory galaxyGeneratorFactory;
        @Mock
        private NoiseGeneratorFactory noiseGeneratorFactory;
        @Mock
        private StarFieldApplicator starFieldApplicator;
        @Mock
        private BloomApplicator bloomApplicator;
        @Mock
        private ImageSerializer imageSerializer;

        @InjectMocks
        private GalaxyService galaxyService;

        @BeforeEach
        void setUp() {
                org.mockito.MockitoAnnotations.openMocks(this);
        }

        @Test
        void shouldFindAllGalaxyImages() {
                GalaxyImage image = new GalaxyImage();
                when(galaxyImageRepository.findAll()).thenReturn(List.of(image));

                List<GalaxyImage> result = galaxyService.findAllGalaxyImages();

                assertThat(result).hasSize(1);
                verify(galaxyImageRepository).findAll();
        }

        @Test
        void shouldCreateGalaxyImage() throws IOException {
                GalaxyRequestCmd cmd = GalaxyRequestCmd.builder()
                                .width(100)
                                .height(100)
                                .build();

                UUID generatedId = UUID.randomUUID();

                GalaxyParameters params = mock(GalaxyParameters.class);
                when(params.getGalaxyType()).thenReturn(GalaxyType.SPIRAL);
                org.dbs.sbgb.domain.model.parameters.MultiLayerNoiseParameters multiLayer = mock(
                                org.dbs.sbgb.domain.model.parameters.MultiLayerNoiseParameters.class);
                when(params.getMultiLayerNoiseParameters()).thenReturn(multiLayer);
                org.dbs.sbgb.domain.model.parameters.DomainWarpParameters warpParams = mock(
                                org.dbs.sbgb.domain.model.parameters.DomainWarpParameters.class);
                when(params.getDomainWarpParameters()).thenReturn(warpParams);
                org.dbs.sbgb.domain.model.parameters.StarFieldParameters starParams = mock(
                                org.dbs.sbgb.domain.model.parameters.StarFieldParameters.class);
                when(params.getStarFieldParameters()).thenReturn(starParams);
                when(galaxyStructureMapper.toGalaxyParameters(cmd)).thenReturn(params);

                GalaxyColorCalculator colorCalculator = mock(GalaxyColorCalculator.class);
                when(colorCalculator.getSpaceBackgroundColor()).thenReturn(Color.BLACK);
                when(colorCalculator.calculateGalaxyColor(anyDouble())).thenReturn(Color.WHITE);
                when(galaxyStructureMapper.createColorCalculator(any())).thenReturn(colorCalculator);

                PerlinGenerator noiseGenerator = mock(PerlinGenerator.class);
                when(noiseGeneratorFactory.createNoiseGenerator(any(), anyLong(), anyInt(), anyInt(), any(), any()))
                                .thenReturn(noiseGenerator);

                GalaxyIntensityCalculator intensityCalculator = mock(GalaxyIntensityCalculator.class);
                when(galaxyGeneratorFactory.create(any(GalaxyType.class), any())).thenReturn(intensityCalculator);

                when(starFieldApplicator.applyIfEnabled(any(), any(), anyLong())).thenAnswer(i -> i.getArgument(0));

                byte[] fakeBytes = new byte[] { 10, 11, 12 };
                when(imageSerializer.toByteArray(any(BufferedImage.class))).thenReturn(fakeBytes);

                GalaxyStructure structure = new GalaxyStructure();
                when(galaxyStructureMapper.toGalaxyStructure(cmd)).thenReturn(structure);

                GalaxyImage savedImage = new GalaxyImage();
                savedImage.setId(generatedId);
                savedImage.setImage(fakeBytes);
                when(galaxyImageRepository.save(any(GalaxyImage.class))).thenReturn(savedImage);

                GalaxyImage result = galaxyService.createGalaxyImage(cmd);

                assertThat(result.getId()).isEqualTo(generatedId);
                assertThat(result.getImage()).isEqualTo(fakeBytes);
                verify(galaxyImageRepository).save(any(GalaxyImage.class));
        }

        @Test
        void shouldUpdateNote() {
                UUID id = UUID.randomUUID();
                int note = 4;

                galaxyService.updateNote(id, note);

                verify(galaxyImageRepository).updateNote(id, note);
        }
}
