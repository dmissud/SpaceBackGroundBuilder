package org.dbs.sbgb.domain.model;

import org.dbs.sbgb.domain.model.parameters.CoreParameters;

/**
 * Shared base class for all galaxy intensity generators.
 * Provides common geometric calculations (distances, normalization).
 */
public abstract class AbstractGalaxyGenerator implements GalaxyIntensityCalculator {

    protected final int width;
    protected final int height;
    protected final double centerX;
    protected final double centerY;
    protected final PerlinGenerator noiseGenerator;
    protected final CoreParameters coreParameters;

    protected AbstractGalaxyGenerator(int width, int height, PerlinGenerator noiseGenerator,
            CoreParameters coreParameters) {
        this.width = width;
        this.height = height;
        this.centerX = width / 2.0;
        this.centerY = height / 2.0;
        this.noiseGenerator = noiseGenerator;
        this.coreParameters = coreParameters;
    }

    /**
     * Common geometric data calculated for each pixel.
     */
    protected static class PixelGeometry {
        public final double dx;
        public final double dy;
        public final double distance;
        public final double normalizedDistance;

        public PixelGeometry(double dx, double dy, double distance, double normalizedDistance) {
            this.dx = dx;
            this.dy = dy;
            this.distance = distance;
            this.normalizedDistance = normalizedDistance;
        }
    }

    /**
     * Calculates the shared geometry (distances) for a given pixel.
     * Retuns null if the pixel is outside the galaxy radius.
     */
    protected PixelGeometry calculateGeometry(int x, int y) {
        double dx = x - centerX;
        double dy = y - centerY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double normalizedDistance = distance / coreParameters.getGalaxyRadius();

        if (normalizedDistance > 1.0) {
            return null; // Outside the galaxy
        }

        return new PixelGeometry(dx, dy, distance, normalizedDistance);
    }
}
