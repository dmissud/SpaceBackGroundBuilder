package org.dbs.sbgb.domain.model.parameters;

import lombok.Builder;
import lombok.Value;

/**
 * Shape parameters specific to lenticular galaxies.
 * Intermediate between elliptical and spiral.
 */
@Value
@Builder
public class LenticularShapeParameters {

    /**
     * Sersic index (n) for the brightness profile.
     * n~2-3 is common for lenticulars.
     */
    @Builder.Default
    double sersicIndex = 2.5;

    /**
     * Axis ratio (b/a) where b=minor axis, a=major axis (0.0-1.0)
     */
    @Builder.Default
    double axisRatio = 0.6;

    /**
     * Orientation angle in degrees (0-360)
     */
    @Builder.Default
    double orientationAngle = 0.0;

    /**
     * Contribution of the disk component relative to the bulge (0.0-1.0)
     */
    @Builder.Default
    double diskContribution = 0.4;
}
