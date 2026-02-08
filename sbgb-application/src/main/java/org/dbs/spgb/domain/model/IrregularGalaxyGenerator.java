package org.dbs.spgb.domain.model;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class IrregularGalaxyGenerator implements GalaxyIntensityCalculator {

    private final int width;
    private final int height;
    private final double centerX;
    private final double centerY;
    private final PerlinGenerator noiseGenerator;
    private final double coreSize;
    private final double galaxyRadius;
    private final double irregularity;
    private final List<Clump> clumps;

    private static class Clump {
        double x, y, size, intensity;

        Clump(double x, double y, double size, double intensity) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.intensity = intensity;
        }
    }

    private IrregularGalaxyGenerator(int width, int height,
                                     PerlinGenerator noiseGenerator,
                                     long seed,
                                     double coreSize,
                                     double galaxyRadius,
                                     double irregularity,
                                     int clumpCount,
                                     double clumpSize) {
        this.width = width;
        this.height = height;
        this.centerX = width / 2.0;
        this.centerY = height / 2.0;
        this.noiseGenerator = noiseGenerator;
        this.coreSize = coreSize;
        this.galaxyRadius = galaxyRadius;
        this.irregularity = irregularity;

        // Generate random clumps of star formation
        this.clumps = generateClumps(seed, clumpCount, clumpSize);
    }

    private List<Clump> generateClumps(long seed, int clumpCount, double clumpSize) {
        List<Clump> clumps = new ArrayList<>();
        Random random = new Random(seed);

        for (int i = 0; i < clumpCount; i++) {
            // Distribute clumps randomly within galaxy radius
            double angle = random.nextDouble() * 2.0 * Math.PI;
            double distance = Math.sqrt(random.nextDouble()) * galaxyRadius * 0.8; // 80% of radius

            double clumpX = centerX + distance * Math.cos(angle);
            double clumpY = centerY + distance * Math.sin(angle);
            double size = clumpSize * (0.5 + random.nextDouble()); // Vary size
            double intensity = 0.6 + random.nextDouble() * 0.4; // 0.6-1.0

            clumps.add(new Clump(clumpX, clumpY, size, intensity));
        }

        return clumps;
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

        // Core contribution (small for irregular galaxies)
        double coreRadius = galaxyRadius * coreSize;
        double coreIntensity = Math.exp(-(distance * distance) / (2.0 * coreRadius * coreRadius));
        coreIntensity *= 0.3; // Weak core

        // Clump contributions (Gaussian profiles)
        double clumpIntensity = 0.0;
        for (Clump clump : clumps) {
            double clumpDx = x - clump.x;
            double clumpDy = y - clump.y;
            double clumpDist = Math.sqrt(clumpDx * clumpDx + clumpDy * clumpDy);
            double clumpProfile = Math.exp(-(clumpDist * clumpDist) / (2.0 * clump.size * clump.size));
            clumpIntensity += clumpProfile * clump.intensity;
        }
        clumpIntensity = Math.min(clumpIntensity, 1.0);

        // Strong Perlin noise for irregularity
        double noiseValue = noiseGenerator.scaleNoiseNormalizedValue(x, y);
        double noiseFactor = (1.0 - irregularity) + (noiseValue * irregularity);

        // Combine all sources
        double baseIntensity = coreIntensity + clumpIntensity * 0.7;

        // Soft radial falloff
        double radialFalloff = Math.pow(1.0 - normalizedDistance, 1.5);

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
        private long seed = 42L;
        private double coreSize = 0.05;
        private double galaxyRadius = 1500.0;
        private double irregularity = 0.8;
        private int clumpCount = 15;
        private double clumpSize = 80.0;

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

        public Builder seed(long seed) {
            this.seed = seed;
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

        public Builder irregularity(double irregularity) {
            this.irregularity = irregularity;
            return this;
        }

        public Builder clumpCount(int clumpCount) {
            this.clumpCount = clumpCount;
            return this;
        }

        public Builder clumpSize(double clumpSize) {
            this.clumpSize = clumpSize;
            return this;
        }

        public IrregularGalaxyGenerator build() {
            if (noiseGenerator == null) {
                throw new IllegalStateException("noiseGenerator must be set");
            }
            return new IrregularGalaxyGenerator(width, height, noiseGenerator, seed,
                    coreSize, galaxyRadius, irregularity, clumpCount, clumpSize);
        }
    }
}
