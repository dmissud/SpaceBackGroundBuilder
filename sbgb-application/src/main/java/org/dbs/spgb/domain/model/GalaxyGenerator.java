package org.dbs.spgb.domain.model;

import lombok.extern.slf4j.Slf4j;

/**
 * Generator for realistic galaxy structures with spiral arms
 * Combines geometric calculations (radial distance, spiral rotation)
 * with Perlin noise for organic appearance
 */
@Slf4j
public class GalaxyGenerator implements GalaxyIntensityCalculator {

    private final int width;
    private final int height;
    private final double centerX;
    private final double centerY;
    private final PerlinGenerator noiseGenerator;

    // Galaxy parameters
    private final int numberOfArms;
    private final double armWidth;
    private final double armRotation;
    private final double coreSize;
    private final double galaxyRadius;

    public GalaxyGenerator(int width, int height,
                          PerlinGenerator noiseGenerator,
                          int numberOfArms,
                          double armWidth,
                          double armRotation,
                          double coreSize,
                          double galaxyRadius) {
        this.width = width;
        this.height = height;
        this.centerX = width / 2.0;
        this.centerY = height / 2.0;
        this.noiseGenerator = noiseGenerator;
        this.numberOfArms = numberOfArms;
        this.armWidth = armWidth;
        this.armRotation = armRotation;
        this.coreSize = coreSize;
        this.galaxyRadius = galaxyRadius;
    }

    @Override
    public double calculateGalaxyIntensity(int x, int y) {
        // Calculate distance from center
        double dx = x - centerX;
        double dy = y - centerY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Calculate angle from center
        double angle = Math.atan2(dy, dx);

        // Normalize distance (0 to 1 across galaxy radius)
        double normalizedDistance = distance / galaxyRadius;

        // Outside galaxy radius, return zero
        if (normalizedDistance > 1.0) {
            return 0.0;
        }

        // Calculate core intensity (bright center, exponential falloff)
        double coreIntensity = calculateCoreIntensity(normalizedDistance);

        // Calculate spiral arm intensity
        double armIntensity = calculateSpiralArmIntensity(angle, normalizedDistance);

        // Add noise for organic look
        double noiseValue = noiseGenerator.scaleNoiseNormalizedValue(x, y);
        double noiseFactor = 0.3 + (noiseValue * 0.7); // Noise modulates intensity

        // Combine core and arms with radial falloff
        double radialFalloff = Math.pow(1.0 - normalizedDistance, 2.0);
        double combinedIntensity = (coreIntensity + armIntensity) * radialFalloff * noiseFactor;

        return Math.clamp(combinedIntensity, 0.0, 1.0);
    }

    /**
     * Calculate core intensity - exponential bright center
     */
    private double calculateCoreIntensity(double normalizedDistance) {
        if (normalizedDistance < coreSize) {
            // Very bright core with exponential falloff
            double coreDistance = normalizedDistance / coreSize;
            return Math.exp(-coreDistance * 3.0) * 2.0;
        }
        return 0.0;
    }

    /**
     * Calculate spiral arm intensity using logarithmic spiral formula
     */
    private double calculateSpiralArmIntensity(double angle, double normalizedDistance) {
        double maxArmIntensity = 0.0;

        // Check each spiral arm
        for (int arm = 0; arm < numberOfArms; arm++) {
            double armBaseAngle = (2.0 * Math.PI * arm) / numberOfArms;

            // Logarithmic spiral: angle = armRotation * ln(distance)
            double spiralAngle = armBaseAngle + armRotation * Math.log(normalizedDistance + 0.1);

            // Normalize angles to [-PI, PI]
            double angleDiff = normalizeAngle(angle - spiralAngle);

            // Calculate distance to spiral arm center
            double armDistance = Math.abs(angleDiff) * normalizedDistance * galaxyRadius / armWidth;

            // Gaussian falloff from arm center
            double armIntensity = Math.exp(-armDistance * armDistance * 0.5);

            maxArmIntensity = Math.max(maxArmIntensity, armIntensity);
        }

        return maxArmIntensity;
    }

    /**
     * Normalize angle to [-PI, PI] range
     */
    private double normalizeAngle(double angle) {
        while (angle > Math.PI) angle -= 2.0 * Math.PI;
        while (angle < -Math.PI) angle += 2.0 * Math.PI;
        return angle;
    }

    /**
     * Create a builder for GalaxyGenerator
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int width = 4000;
        private int height = 4000;
        private PerlinGenerator noiseGenerator;
        private int numberOfArms = 2;
        private double armWidth = 80.0;
        private double armRotation = 4.0;
        private double coreSize = 0.05;
        private double galaxyRadius = 1000.0;

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder noiseGenerator(PerlinGenerator noiseGenerator) {
            this.noiseGenerator = noiseGenerator;
            return this;
        }

        public Builder numberOfArms(int numberOfArms) {
            this.numberOfArms = numberOfArms;
            return this;
        }

        public Builder armWidth(double armWidth) {
            this.armWidth = armWidth;
            return this;
        }

        public Builder armRotation(double armRotation) {
            this.armRotation = armRotation;
            return this;
        }

        public Builder coreSize(double coreSize) {
            this.coreSize = coreSize;
            return this;
        }

        public Builder galaxyRadius(double galaxyRadius) {
            this.galaxyRadius = galaxyRadius;
            return this;
        }

        public GalaxyGenerator build() {
            if (noiseGenerator == null) {
                throw new IllegalStateException("noiseGenerator must be set");
            }
            return new GalaxyGenerator(width, height, noiseGenerator,
                numberOfArms, armWidth, armRotation, coreSize, galaxyRadius);
        }
    }
}
