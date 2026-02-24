package org.dbs.sbgb.domain.model.parameters;

import lombok.Builder;
import lombok.Value;

/**
 * Shape parameters specific to elliptical galaxies.
 * Defines the Sersic profile and elliptical shape.
 */
@Value
@Builder
public class EllipticalShapeParameters {

    /**
     * Sersic index (n) for the brightness profile.
     * n=1: exponential, n=4: de Vaucouleurs (typical elliptical)
     */
    @Builder.Default
    double sersicIndex = 4.0;

    /**
     * Axis ratio (b/a) where b=minor axis, a=major axis (0.0-1.0)
     * 1.0 = circular, lower values = more elongated
     */
    @Builder.Default
    double axisRatio = 0.7;

    /**
     * Orientation angle in degrees (0-360)
     */
    @Builder.Default
    double orientationAngle = 45.0;
}
