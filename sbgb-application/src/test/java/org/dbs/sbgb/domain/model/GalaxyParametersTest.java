package org.dbs.sbgb.domain.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class GalaxyParametersTest {

    @Test
    void createVibrantSpiral_shouldReturnParametersWithAllFeaturesEnabled() {
        // When
        GalaxyParameters params = GalaxyParameters.createVibrantSpiral();

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
        assertThat(params.getSpiralParameters()).isNotNull();
        assertThat(params.getSpiralParameters().getNumberOfArms()).isEqualTo(3);
    }
}
