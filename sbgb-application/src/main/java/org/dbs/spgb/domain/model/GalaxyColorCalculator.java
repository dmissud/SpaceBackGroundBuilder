package org.dbs.spgb.domain.model;

import java.awt.*;

/**
 * Interface for calculating galaxy colors based on intensity
 */
public interface GalaxyColorCalculator {

    /**
     * Calculate color for a given galaxy intensity (0.0 to 1.0)
     * @param intensity Galaxy intensity at this point
     * @return Color for this pixel
     */
    Color calculateGalaxyColor(double intensity);

    /**
     * Get the background color for empty space
     */
    Color getSpaceBackgroundColor();
}
