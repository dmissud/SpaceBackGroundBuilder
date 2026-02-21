package org.dbs.sbgb.domain.model.parameters;

import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EllipticalPresetsTest {

    @Test
    void giantPresetShouldHaveHighSersicIndexAndNearlyRoundShape() {
        GalaxyParameters params = GalaxyParameters.createGiantElliptical();

        EllipticalShapeParameters elliptical = params.getEllipticalParameters();
        assertThat(params.getGalaxyType()).isEqualTo(GalaxyType.ELLIPTICAL);
        assertThat(elliptical.getSersicIndex()).isGreaterThanOrEqualTo(7.0);
        assertThat(elliptical.getAxisRatio()).isGreaterThanOrEqualTo(0.8);
    }

    @Test
    void giantPresetShouldHaveLargeRadius() {
        GalaxyParameters params = GalaxyParameters.createGiantElliptical();

        assertThat(params.getCoreParameters().getGalaxyRadius()).isGreaterThanOrEqualTo(1800.0);
    }

    @Test
    void lenticularPresetShouldHaveLowSersicIndexAndFlatShape() {
        GalaxyParameters params = GalaxyParameters.createLenticularElliptical();

        EllipticalShapeParameters elliptical = params.getEllipticalParameters();
        assertThat(params.getGalaxyType()).isEqualTo(GalaxyType.ELLIPTICAL);
        assertThat(elliptical.getSersicIndex()).isLessThanOrEqualTo(2.0);
        assertThat(elliptical.getAxisRatio()).isLessThanOrEqualTo(0.35);
    }

    @Test
    void lenticularPresetShouldBeOrientedEdgeOn() {
        GalaxyParameters params = GalaxyParameters.createLenticularElliptical();

        double angle = params.getEllipticalParameters().getOrientationAngle();
        assertThat(angle).isBetween(80.0, 100.0);
    }
}
