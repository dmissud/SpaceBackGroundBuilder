package org.dbs.sbgb.domain.service;

import org.dbs.sbgb.port.in.GalaxyRequestCmd;
import org.dbs.sbgb.port.out.GalaxyBaseStructureRepository;
import org.dbs.sbgb.port.out.GalaxyCosmeticRenderRepository;
import org.dbs.sbgb.port.out.GalaxyImageComputationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GalaxyServiceTest {

    @Mock private GalaxyBaseStructureRepository baseStructureRepository;
    @Mock private GalaxyCosmeticRenderRepository cosmeticRenderRepository;
    @Mock private ImageSerializer imageSerializer;
    @Mock private GalaxyImageComputationPort galaxyImageComputationPort;

    private GalaxyService galaxyService;

    @BeforeEach
    void setUp() {
        org.mockito.MockitoAnnotations.openMocks(this);
        galaxyService = new GalaxyService(baseStructureRepository, cosmeticRenderRepository,
                imageSerializer, galaxyImageComputationPort);
    }

    @Test
    void shouldBuildGalaxyImage() throws IOException {
        GalaxyRequestCmd cmd = GalaxyRequestCmd.builder()
                .width(100).height(100)
                .noiseParameters(new org.dbs.sbgb.port.in.NoiseParameters(4, 0.5, 2.0, 1.0))
                .multiLayerNoiseParameters(new org.dbs.sbgb.port.in.MultiLayerNoiseParameters(false, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0))
                .build();

        BufferedImage mockImage = mock(BufferedImage.class);
        when(galaxyImageComputationPort.computeImage(anyInt(), eq(cmd))).thenReturn(mockImage);

        byte[] fakeBytes = new byte[]{10, 11, 12};
        when(imageSerializer.toByteArray(mockImage)).thenReturn(fakeBytes);

        byte[] result = galaxyService.buildGalaxyImage(cmd);

        assertThat(result).isEqualTo(fakeBytes);
        verify(imageSerializer).toByteArray(mockImage);
    }
}
