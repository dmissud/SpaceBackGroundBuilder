package org.dbs.sbgb.domain.model;

import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.constant.NoiseModulationConstants;

@Slf4j
public class EllipticalGalaxyGenerator implements GalaxyIntensityCalculator {

    private final int width;
    private final int height;
    private final double centerX;
    private final double centerY;
    private final PerlinGenerator noiseGenerator;
    private final double coreSize;
    private final double galaxyRadius;
    private final double sersicIndex;
    private final double axisRatio;
    private final double orientationAngleRad;
    private final double effectiveRadius;
    private final double bn;

    private EllipticalGalaxyGenerator(int width, int height,
                                      PerlinGenerator noiseGenerator,
                                      double coreSize,
                                      double galaxyRadius,
                                      double sersicIndex,
                                      double axisRatio,
                                      double orientationAngle) {
        this.width = width;
        this.height = height;
        this.centerX = width / 2.0;
        this.centerY = height / 2.0;
        this.noiseGenerator = noiseGenerator;
        this.coreSize = coreSize;
        this.galaxyRadius = galaxyRadius;
        this.sersicIndex = sersicIndex;
        this.axisRatio = axisRatio;
        this.orientationAngleRad = Math.toRadians(orientationAngle);
        this.effectiveRadius = galaxyRadius * 0.5;
        this.bn = 1.9992 * sersicIndex - 0.3271;
    }

    @Override
    public double calculateGalaxyIntensity(int x, int y) {
        double dx = x - centerX;
        double dy = y - centerY;

        // Rotation for ellipse orientation
        double cosA = Math.cos(orientationAngleRad);
        double sinA = Math.sin(orientationAngleRad);
        double rotX = dx * cosA + dy * sinA;
        double rotY = -dx * sinA + dy * cosA;

        // Elliptical distance
        double ellipticalDistance = Math.sqrt(rotX * rotX + (rotY * rotY) / (axisRatio * axisRatio));
        double normalizedDistance = ellipticalDistance / galaxyRadius;

        if (normalizedDistance > 1.0) {
            return 0.0;
        }

        // Sersic profile
        double rRatio = ellipticalDistance / effectiveRadius;
        double sersicIntensity = Math.exp(-bn * (Math.pow(rRatio, 1.0 / sersicIndex) - 1.0));

        // Perlin noise (subtle for ellipticals)
        double noiseValue = noiseGenerator.scaleNoiseNormalizedValue(x, y);
        double noiseFactor = NoiseModulationConstants.ELLIPTICAL_NOISE_BASE
                + (noiseValue * NoiseModulationConstants.ELLIPTICAL_NOISE_RANGE);

        // Smooth radial falloff
        double radialFalloff = Math.pow(1.0 - normalizedDistance, 1.5);

        double combined = sersicIntensity * radialFalloff * noiseFactor;
        return Math.clamp(combined, 0.0, 1.0);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int width = 4000;
        private int height = 4000;
        private PerlinGenerator noiseGenerator;
        private double coreSize = 0.05;
        private double galaxyRadius = 1500.0;
        private double sersicIndex = 4.0;
        private double axisRatio = 0.7;
        private double orientationAngle = 0.0;

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

        public Builder coreSize(double coreSize) {
            this.coreSize = coreSize;
            return this;
        }

        public Builder galaxyRadius(double galaxyRadius) {
            this.galaxyRadius = galaxyRadius;
            return this;
        }

        public Builder sersicIndex(double sersicIndex) {
            this.sersicIndex = sersicIndex;
            return this;
        }

        public Builder axisRatio(double axisRatio) {
            this.axisRatio = axisRatio;
            return this;
        }

        public Builder orientationAngle(double orientationAngle) {
            this.orientationAngle = orientationAngle;
            return this;
        }

        public EllipticalGalaxyGenerator build() {
            if (noiseGenerator == null) {
                throw new IllegalStateException("noiseGenerator must be set");
            }
            return new EllipticalGalaxyGenerator(width, height, noiseGenerator,
                    coreSize, galaxyRadius, sersicIndex, axisRatio, orientationAngle);
        }
    }
}
