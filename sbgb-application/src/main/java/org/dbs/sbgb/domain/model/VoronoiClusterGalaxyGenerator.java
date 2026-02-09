package org.dbs.sbgb.domain.model;

import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.constant.CoreIntensityConstants;
import org.dbs.sbgb.domain.constant.NoiseModulationConstants;
import org.dbs.sbgb.domain.constant.RadialFalloffConstants;
import org.dbs.sbgb.domain.model.parameters.CoreParameters;
import org.dbs.sbgb.domain.model.parameters.VoronoiClusterParameters;

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
    private final CoreParameters coreParameters;
    private final VoronoiClusterParameters voronoiParameters;
    private final List<ClusterCenter> clusters;

    private VoronoiClusterGalaxyGenerator(int width, int height,
                                          PerlinGenerator noiseGenerator,
                                          long seed,
                                          CoreParameters coreParameters,
                                          VoronoiClusterParameters voronoiParameters) {
        this.width = width;
        this.height = height;
        this.centerX = width / 2.0;
        this.centerY = height / 2.0;
        this.noiseGenerator = noiseGenerator;
        this.coreParameters = coreParameters;
        this.voronoiParameters = voronoiParameters;
        this.clusters = generateClusters(seed, voronoiParameters.getClusterCount(), voronoiParameters.getClusterConcentration());
    }

    private List<ClusterCenter> generateClusters(long seed, int clusterCount, double concentration) {
        Random random = new Random(seed);
        List<ClusterCenter> result = new ArrayList<>(clusterCount);

        for (int i = 0; i < clusterCount; i++) {
            // Distance from center using exponential concentration
            double distance = coreParameters.getGalaxyRadius() * Math.pow(random.nextDouble(), 1.0 / (1.0 - concentration + 0.01));
            distance = Math.min(distance, coreParameters.getGalaxyRadius() * 0.95);

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
        double normalizedDistance = distance / coreParameters.getGalaxyRadius();

        if (normalizedDistance > 1.0) {
            return 0.0;
        }

        // Core intensity (same pattern as spiral)
        double coreIntensity = (normalizedDistance < coreParameters.getCoreSize())
                ? Math.exp(-(normalizedDistance / coreParameters.getCoreSize()) * CoreIntensityConstants.CORE_EXPONENTIAL_FALLOFF)
                        * CoreIntensityConstants.CORE_BRIGHTNESS_MULTIPLIER
                : 0.0;

        // Cluster intensity: sum of Gaussian contributions
        double clusterIntensity = 0.0;
        for (ClusterCenter cluster : clusters) {
            double cdx = x - cluster.x;
            double cdy = y - cluster.y;
            double cdist = Math.sqrt(cdx * cdx + cdy * cdy);
            clusterIntensity += cluster.brightness * Math.exp(-(cdist * cdist) / (2 * voronoiParameters.getClusterSize() * voronoiParameters.getClusterSize()));
        }

        // Perlin noise modulation
        double noiseValue = noiseGenerator.scaleNoiseNormalizedValue(x, y);
        double noiseFactor = NoiseModulationConstants.NOISE_BASE_CONTRIBUTION
                + (noiseValue * NoiseModulationConstants.NOISE_MODULATION_RANGE);

        // Radial falloff
        double radialFalloff = Math.pow(1.0 - normalizedDistance, RadialFalloffConstants.STANDARD_FALLOFF_EXPONENT);

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
        private CoreParameters coreParameters = CoreParameters.builder()
                .coreSize(0.05)
                .galaxyRadius(1500.0)
                .build();
        private VoronoiClusterParameters voronoiParameters = VoronoiClusterParameters.builder()
                .clusterCount(80)
                .clusterSize(60.0)
                .clusterConcentration(0.7)
                .build();

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

        public Builder coreParameters(CoreParameters coreParameters) {
            this.coreParameters = coreParameters;
            return this;
        }

        public Builder voronoiParameters(VoronoiClusterParameters voronoiParameters) {
            this.voronoiParameters = voronoiParameters;
            return this;
        }

        public VoronoiClusterGalaxyGenerator build() {
            if (noiseGenerator == null) {
                throw new IllegalStateException("noiseGenerator must be set");
            }
            return new VoronoiClusterGalaxyGenerator(width, height, noiseGenerator, seed, coreParameters, voronoiParameters);
        }
    }

    private record ClusterCenter(double x, double y, double brightness) {
    }
}
