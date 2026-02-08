package org.dbs.spgb.domain.model;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class VoronoiClusterGalaxyGenerator implements GalaxyIntensityCalculator {

    private final int width;
    private final int height;
    private final double centerX;
    private final double centerY;
    private final PerlinGenerator noiseGenerator;
    private final double coreSize;
    private final double galaxyRadius;
    private final double clusterSize;
    private final List<ClusterCenter> clusters;

    private VoronoiClusterGalaxyGenerator(int width, int height,
                                          PerlinGenerator noiseGenerator,
                                          long seed,
                                          double coreSize,
                                          double galaxyRadius,
                                          int clusterCount,
                                          double clusterSize,
                                          double clusterConcentration) {
        this.width = width;
        this.height = height;
        this.centerX = width / 2.0;
        this.centerY = height / 2.0;
        this.noiseGenerator = noiseGenerator;
        this.coreSize = coreSize;
        this.galaxyRadius = galaxyRadius;
        this.clusterSize = clusterSize;
        this.clusters = generateClusters(seed, clusterCount, clusterConcentration);
    }

    private List<ClusterCenter> generateClusters(long seed, int clusterCount, double concentration) {
        Random random = new Random(seed);
        List<ClusterCenter> result = new ArrayList<>(clusterCount);

        for (int i = 0; i < clusterCount; i++) {
            // Distance from center using exponential concentration
            double distance = galaxyRadius * Math.pow(random.nextDouble(), 1.0 / (1.0 - concentration + 0.01));
            distance = Math.min(distance, galaxyRadius * 0.95);

            // Random angle
            double angle = random.nextDouble() * 2.0 * Math.PI;

            double x = centerX + distance * Math.cos(angle);
            double y = centerY + distance * Math.sin(angle);

            // Random brightness for each cluster
            double brightness = 0.3 + random.nextDouble() * 0.7;

            result.add(new ClusterCenter(x, y, brightness));
        }

        return result;
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

        // Core intensity (same pattern as spiral)
        double coreIntensity = (normalizedDistance < coreSize)
                ? Math.exp(-(normalizedDistance / coreSize) * 3.0) * 2.0
                : 0.0;

        // Cluster intensity: sum of Gaussian contributions
        double clusterIntensity = 0.0;
        for (ClusterCenter cluster : clusters) {
            double cdx = x - cluster.x;
            double cdy = y - cluster.y;
            double cdist = Math.sqrt(cdx * cdx + cdy * cdy);
            clusterIntensity += cluster.brightness * Math.exp(-(cdist * cdist) / (2 * clusterSize * clusterSize));
        }

        // Perlin noise modulation
        double noiseValue = noiseGenerator.scaleNoiseNormalizedValue(x, y);
        double noiseFactor = 0.3 + (noiseValue * 0.7);

        // Radial falloff
        double radialFalloff = Math.pow(1.0 - normalizedDistance, 2.0);

        double combined = (coreIntensity + clusterIntensity) * radialFalloff * noiseFactor;
        return Math.clamp(combined, 0.0, 1.0);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int width = 4000;
        private int height = 4000;
        private PerlinGenerator noiseGenerator;
        private long seed = 0L;
        private double coreSize = 0.05;
        private double galaxyRadius = 1500.0;
        private int clusterCount = 80;
        private double clusterSize = 60.0;
        private double clusterConcentration = 0.7;

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

        public Builder clusterCount(int clusterCount) {
            this.clusterCount = clusterCount;
            return this;
        }

        public Builder clusterSize(double clusterSize) {
            this.clusterSize = clusterSize;
            return this;
        }

        public Builder clusterConcentration(double clusterConcentration) {
            this.clusterConcentration = clusterConcentration;
            return this;
        }

        public VoronoiClusterGalaxyGenerator build() {
            if (noiseGenerator == null) {
                throw new IllegalStateException("noiseGenerator must be set");
            }
            return new VoronoiClusterGalaxyGenerator(width, height, noiseGenerator,
                    seed, coreSize, galaxyRadius, clusterCount, clusterSize, clusterConcentration);
        }
    }

    private record ClusterCenter(double x, double y, double brightness) {
    }
}
