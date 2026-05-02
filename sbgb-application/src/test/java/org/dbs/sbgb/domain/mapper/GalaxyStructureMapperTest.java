package org.dbs.sbgb.domain.mapper;

import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
import org.dbs.sbgb.port.in.DensityWaveParameters;
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
    void toGalaxyParameters_withCustomDensityWaveParams_shouldUseProvidedValues() {
        // Given
        DensityWaveParameters customParams = new DensityWaveParameters(
                20000.0f, 3000.0f, 0.025f, 0.80f, 0.92f, 40000, false, 3, 60.0f, 5000.0f, 3
        );
        GalaxyRequestCmd cmd = GalaxyRequestCmd.builder()
                .galaxyType("DENSITY_WAVE")
                .densityWaveParameters(customParams)
                .width(800)
                .height(800)
                .build();

        // When
        GalaxyParameters params = mapper.toGalaxyParameters(cmd);

        // Then
        assertThat(params.getGalaxyType()).isEqualTo(GalaxyType.DENSITY_WAVE);
        assertThat(params.getDensityWaveParams()).isNotNull();
        assertThat(params.getDensityWaveParams().galaxyRadius()).isEqualTo(20000.0f);
        assertThat(params.getDensityWaveParams().starCount()).isEqualTo(40000);
        assertThat(params.getDensityWaveParams().pertN()).isEqualTo(3);
    }

    @Test
    void toGalaxyParameters_withDensityWaveTypeButNoParams_shouldUseDefaults() {
        // Given
        GalaxyRequestCmd cmd = GalaxyRequestCmd.builder()
                .galaxyType("DENSITY_WAVE")
                .width(800)
                .height(800)
                .build();

        // When
        GalaxyParameters params = mapper.toGalaxyParameters(cmd);

        // Then
        assertThat(params.getGalaxyType()).isEqualTo(GalaxyType.DENSITY_WAVE);
        assertThat(params.getDensityWaveParams()).isNotNull();
        assertThat(params.getDensityWaveParams().starCount()).isEqualTo(60000);
    }

    @Test
    void toGalaxyParameters_withDensityWaveType_shouldHaveNonNullRequiredParameters() {
        // Given
        GalaxyRequestCmd cmd = GalaxyRequestCmd.builder()
                .galaxyType("DENSITY_WAVE")
                .width(800)
                .height(800)
                .build();

        // When
        GalaxyParameters params = mapper.toGalaxyParameters(cmd);

        // Then
        assertThat(params.getMultiLayerNoiseParameters()).isNotNull();
        assertThat(params.getMultiLayerNoiseParameters().isEnabled()).isFalse();
        assertThat(params.getDomainWarpParameters()).isNotNull();
        assertThat(params.getStarFieldParameters()).isNotNull();
        assertThat(params.getBloomParameters()).isNotNull();
        assertThat(params.getNoiseTextureParameters()).isNotNull();
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
