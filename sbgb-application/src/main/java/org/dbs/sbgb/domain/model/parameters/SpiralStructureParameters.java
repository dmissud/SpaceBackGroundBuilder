package org.dbs.sbgb.domain.model.parameters;

import lombok.Builder;
import lombok.Value;

/**
 * Structure parameters specific to spiral galaxies.
 * Defines the spiral arm configuration.
 */
@Value
@Builder
public class SpiralStructureParameters {

    /**
     * Number of spiral arms (typically 2-4)
     */
    int numberOfArms;

    /**
     * Width of each spiral arm in pixels
     */
    double armWidth;

    /**
     * Rotation coefficient for spiral arms (controls tightness)
     */
    double armRotation;

    /**
     * Opacity of the dark dust lanes (0.0 to 1.0)
     */
    @Builder.Default
    double darkLaneOpacity = 0.0;
}
