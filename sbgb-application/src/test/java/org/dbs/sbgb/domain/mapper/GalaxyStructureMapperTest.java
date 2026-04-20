package org.dbs.sbgb.domain.mapper;

import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
import org.dbs.sbgb.port.in.GalaxyRequestCmd;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GalaxyStructureMapperTest {

    private final GalaxyStructureMapper mapper = new GalaxyStructureMapper();

    @Test
    void toGalaxyParameters_withVibrantSpiralPreset_shouldReturnVibrantSpiralParameters() {
        // Given
        GalaxyRequestCmd cmd = GalaxyRequestCmd.builder()
                .preset("VIBRANT_SPIRAL")
                .width(500)
                .height(500)
                .build();

        // When
        GalaxyParameters params = mapper.toGalaxyParameters(cmd);

        // Then
        assertThat(params.getGalaxyType()).isEqualTo(GalaxyType.SPIRAL);
        assertThat(params.getDomainWarpParameters()).isNotNull();
        assertThat(params.getDomainWarpParameters().isEnabled()).isTrue();
        assertThat(params.getMultiLayerNoiseParameters()).isNotNull();
        assertThat(params.getMultiLayerNoiseParameters().isEnabled()).isTrue();
        assertThat(params.getStarFieldParameters()).isNotNull();
        assertThat(params.getStarFieldParameters().isEnabled()).isTrue();
        assertThat(params.getBloomParameters()).isNotNull();
        assertThat(params.getBloomParameters().isEnabled()).isTrue();
    }

    @Test
    void toGalaxyParameters_withUnknownPreset_shouldFallbackToDefault() {
        // Given
        GalaxyRequestCmd cmd = GalaxyRequestCmd.builder()
                .preset("UNKNOWN_PRESET")
                .width(500)
                .height(500)
                .build();

        // When
        GalaxyParameters params = mapper.toGalaxyParameters(cmd);

        // Then
        assertThat(params.getGalaxyType()).isEqualTo(GalaxyType.SPIRAL);
        assertThat(params.getDomainWarpParameters()).isNotNull();
        assertThat(params.getDomainWarpParameters().getWarpStrength()).isEqualTo(0.0); // Default has no domain warp
    }

    @Test
    void toGalaxyParameters_withoutPreset_shouldBuildFromIndividualParameters() {
        // Given
        GalaxyRequestCmd cmd = GalaxyRequestCmd.builder()
                .galaxyType("SPIRAL")
                .width(500)
                .height(500)
                .build();

        // When
        GalaxyParameters params = mapper.toGalaxyParameters(cmd);

        // Then
        assertThat(params.getGalaxyType()).isEqualTo(GalaxyType.SPIRAL);
        // Should have default parameters
        assertThat(params.getSpiralParameters()).isNotNull();
        assertThat(params.getSpiralParameters().getNumberOfArms()).isEqualTo(2); // Default
        assertThat(params.getDomainWarpParameters()).isNotNull();
        assertThat(params.getDomainWarpParameters().getWarpStrength()).isEqualTo(0.0);
    }
}
