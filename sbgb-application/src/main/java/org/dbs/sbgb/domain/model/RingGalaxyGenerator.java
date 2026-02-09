package org.dbs.sbgb.domain.model;

import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.constant.NoiseModulationConstants;
import org.dbs.sbgb.domain.constant.RadialFalloffConstants;

@Slf4j
public class RingGalaxyGenerator implements GalaxyIntensityCalculator {

    private final int width;
    private final int height;
    private final double centerX;
    private final double centerY;
    private final PerlinGenerator noiseGenerator;
    private final double coreSize;
    private final double galaxyRadius;
    private final double ringRadius;
    private final double ringWidth;
    private final double ringIntensity;
    private final double coreToRingRatio;

    private RingGalaxyGenerator(int width, int height,
                                PerlinGenerator noiseGenerator,
                                double coreSize,
                                double galaxyRadius,
                                double ringRadius,
                                double ringWidth,
                                double ringIntensity,
                                double coreToRingRatio) {
        this.width = width;
        this.height = height;
        this.centerX = width / 2.0;
        this.centerY = height / 2.0;
        this.noiseGenerator = noiseGenerator;
        this.coreSize = coreSize;
        this.galaxyRadius = galaxyRadius;
        this.ringRadius = ringRadius;
        this.ringWidth = ringWidth;
        this.ringIntensity = ringIntensity;
        this.coreToRingRatio = coreToRingRatio;
    }

    @Override
    public double calculateGalaxyIntensity(int x, int y) {
        double dx = x - centerX;
        double dy = y - centerY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double normalizedDistance = distance / galaxyRadius;

        if (normalizedDistance > 1.0) {
            return 0.0;
        }

        // Core contribution (Gaussian)
        double coreRadius = galaxyRadius * coreSize;
        double coreIntensity = Math.exp(-(distance * distance) / (2.0 * coreRadius * coreRadius));
        coreIntensity *= coreToRingRatio;

        // Ring contribution (Gaussian profile centered on ringRadius)
        double ringDistance = Math.abs(distance - ringRadius);
        double ringProfile = Math.exp(-(ringDistance * ringDistance) / (2.0 * ringWidth * ringWidth));
        double ringContribution = ringProfile * ringIntensity;

        // Combined intensity
        double baseIntensity = coreIntensity + ringContribution;

        // Perlin noise for texture
        double noiseValue = noiseGenerator.scaleNoiseNormalizedValue(x, y);
        double noiseFactor = NoiseModulationConstants.RING_NOISE_BASE
                + (noiseValue * NoiseModulationConstants.RING_NOISE_RANGE);

        // Smooth radial falloff to fade at edges
        double radialFalloff = Math.pow(1.0 - normalizedDistance, RadialFalloffConstants.STANDARD_FALLOFF_EXPONENT);

        double combined = baseIntensity * radialFalloff * noiseFactor;
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
        private double ringRadius = 900.0;
        private double ringWidth = 150.0;
        private double ringIntensity = 1.0;
        private double coreToRingRatio = 0.3;

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

        public Builder ringRadius(double ringRadius) {
            this.ringRadius = ringRadius;
            return this;
        }

        public Builder ringWidth(double ringWidth) {
            this.ringWidth = ringWidth;
            return this;
        }

        public Builder ringIntensity(double ringIntensity) {
            this.ringIntensity = ringIntensity;
            return this;
        }

        public Builder coreToRingRatio(double coreToRingRatio) {
            this.coreToRingRatio = coreToRingRatio;
            return this;
        }

        public RingGalaxyGenerator build() {
            if (noiseGenerator == null) {
                throw new IllegalStateException("noiseGenerator must be set");
            }
            return new RingGalaxyGenerator(width, height, noiseGenerator,
                    coreSize, galaxyRadius, ringRadius, ringWidth, ringIntensity, coreToRingRatio);
        }
    }
}
