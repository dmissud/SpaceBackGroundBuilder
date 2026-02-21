package org.dbs.sbgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import de.articdive.jnoise.pipeline.JNoise;
import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.model.parameters.CoreParameters;
import org.dbs.sbgb.domain.model.parameters.VoronoiClusterParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

/**
 * Implementation of Voronoi Cluster Galaxy Generator using JNoise 4.1.0 and
 * Virtual Threads.
 * Uses an exponential distribution for cluster center placement.
 */
@Slf4j
public class VoronoiClusterGalaxyGenerator implements GalaxyIntensityCalculator {

    private final int width;
    private final int height;
    private final double centerX;
    private final double centerY;
    private final CoreParameters coreParameters;
    private final VoronoiClusterParameters voronoiParameters;
    private final List<ClusterCenter> clusters;
    private final JNoise jNoise;

    public VoronoiClusterGalaxyGenerator(int width, int height,
                                         long seed,
                                         CoreParameters coreParameters,
                                         VoronoiClusterParameters voronoiParameters) {
        this.width = width;
        this.height = height;
        this.centerX = width / 2.0;
        this.centerY = height / 2.0;
        this.coreParameters = coreParameters;
        this.voronoiParameters = voronoiParameters;
        this.clusters = generateClusters(seed, voronoiParameters.getClusterCount(),
                voronoiParameters.getClusterConcentration());

        // Initialize JNoise 4.1.0 pipeline
        this.jNoise = JNoise.newBuilder()
                .perlin(seed, Interpolation.COSINE, FadeFunction.CUBIC_POLY)
                .scale(0.01)
                .build();
    }

    private List<ClusterCenter> generateClusters(long seed, int clusterCount, double concentration) {
        Random random = new Random(seed);
        List<ClusterCenter> result = new ArrayList<>(clusterCount);

        for (int i = 0; i < clusterCount; i++) {
            // Exponential distribution for radial distance: r = -1/lambda * ln(1-u)
            // lambda controlled by concentration
            double lambda = 5.0 * concentration + 1.0;
            double u = random.nextDouble() * 0.95; // Avoid edge
            double r = (-1.0 / lambda) * Math.log(1.0 - u) * coreParameters.getGalaxyRadius();

            double angle = random.nextDouble() * 2.0 * Math.PI;
            double x = centerX + r * Math.cos(angle);
            double y = centerY + r * Math.sin(angle);
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

        if (normalizedDistance > 1.0)
            return 0.0;

        // Core intensity
        double coreIntensity = (normalizedDistance < coreParameters.getCoreSize())
                ? Math.exp(-(normalizedDistance / coreParameters.getCoreSize()) * 4.0)
                : 0.0;

        // Cluster intensity (Gaussian sum)
        double clusterIntensity = 0.0;
        double clusterSize = voronoiParameters.getClusterSize();
        for (ClusterCenter cluster : clusters) {
            double cdx = x - cluster.x;
            double cdy = y - cluster.y;
            double cdistSq = cdx * cdx + cdy * cdy;
            clusterIntensity += cluster.brightness * Math.exp(-cdistSq / (2.0 * clusterSize * clusterSize));
        }

        // Noise Modulation
        double perlinNoise = jNoise.evaluateNoise(x, y);
        double combined = (coreIntensity + clusterIntensity) * (0.2 + 0.8 * perlinNoise);

        return Math.clamp(combined, 0.0, 1.0);
    }

    /**
     * Parallel batch rendering using Java 21 Virtual Threads.
     */
    public float[] generateBuffer() {
        float[] buffer = new float[width * height];
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int y = 0; y < height; y++) {
                final int currentY = y;
                executor.submit(() -> {
                    for (int x = 0; x < width; x++) {
                        buffer[currentY * width + x] = (float) calculateGalaxyIntensity(x, currentY);
                    }
                });
            }
        }
        log.info("Voronoi Cluster Galaxy buffer generation completed ({}x{})", width, height);
        return buffer;
    }

    public static Builder builder() {
        return new Builder();
    }

    private record ClusterCenter(double x, double y, double brightness) {
    }

    public static class Builder {
        private int width = 1000;
        private int height = 1000;
        private long seed = 12345L;
        private CoreParameters coreParameters;
        private VoronoiClusterParameters voronoiParameters;

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder seed(long seed) {
            this.seed = seed;
            return this;
        }

        public Builder coreParameters(CoreParameters cp) {
            this.coreParameters = cp;
            return this;
        }

        public Builder voronoiParameters(VoronoiClusterParameters vp) {
            this.voronoiParameters = vp;
            return this;
        }

        public VoronoiClusterGalaxyGenerator build() {
            return new VoronoiClusterGalaxyGenerator(width, height, seed, coreParameters, voronoiParameters);
        }
    }
}
