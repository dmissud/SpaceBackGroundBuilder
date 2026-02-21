package org.dbs.sbgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import de.articdive.jnoise.pipeline.JNoise;
import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.constant.CoreIntensityConstants;
import org.dbs.sbgb.domain.constant.NoiseModulationConstants;
import org.dbs.sbgb.domain.constant.RadialFalloffConstants;
import org.dbs.sbgb.domain.model.parameters.CoreParameters;
import org.dbs.sbgb.domain.model.parameters.SpiralStructureParameters;

import java.util.concurrent.Executors;

/**
 * Implementation of Spiral Galaxy Generator using JNoise 4.1.0 and Virtual
 * Threads.
 * Follows the specific mathematical requirements for logarithmic spiral arms.
 */
@Slf4j
public class SpiralGalaxyGenerator extends AbstractGalaxyGenerator {

    private final SpiralStructureParameters spiralParameters;
    private final JNoise jNoise;

    public SpiralGalaxyGenerator(int width, int height,
                                 long seed,
            CoreParameters coreParameters,
            SpiralStructureParameters spiralParameters) {
        super(width, height, null, coreParameters);
        this.spiralParameters = spiralParameters;

        // Initialize JNoise 4.1.0 pipeline for dust/grain modulation
        this.jNoise = JNoise.newBuilder()
                .perlin(seed, Interpolation.COSINE, FadeFunction.CUBIC_POLY)
                .scale(0.01) // Adjustable scale for grain
                .build();
    }

    @Override
    public double calculateGalaxyIntensity(int x, int y) {
        PixelGeometry geo = calculateGeometry(x, y);
        if (geo == null)
            return 0.0;

        double r = geo.distance;
        double c = coreParameters.getCoreSize() * coreParameters.getGalaxyRadius();
        double angle = Math.atan2(geo.dy, geo.dx);

        // Core Intensity (Exponential)
        double coreIntensity = Math.exp(-r / c);

        // Spiral Arm Intensity
        double armIntensity = calculateSpiralArmIntensity(angle, r, c);

        // Geometric Intensity
        double geometricIntensity = Math.max(coreIntensity, armIntensity);

        // Noise Modulation (JNoise 4.1.0)
        double perlinNoise = jNoise.evaluateNoise(x, y);
        // Intensity formula: (0.2 + 0.8 * perlin_noise) * geometric_intensity
        double finalIntensity = (0.2 + 0.8 * perlinNoise) * geometricIntensity;

        return Math.clamp(finalIntensity, 0.0, 1.0);
    }

    private double calculateSpiralArmIntensity(double angle, double r, double c) {
        if (r < 1.0)
            return 1.0; // Avoid ln(0)

        double maxArmIntensity = 0.0;
        int n = spiralParameters.getNumberOfArms();
        double rotation = spiralParameters.getArmRotation();

        for (int i = 0; i < n; i++) {
            // Formula: θ = armRotation * ln(r/coreSize) + (2π * armIndex / numberOfArms)
            double theta = rotation * Math.log(r / c) + (2.0 * Math.PI * i / n);

            double deltaPhi = normalizeAngle(angle - theta);

            // Distance to arm center (angular distance converted to arc distance)
            double armDist = Math.abs(deltaPhi) * r;

            // Gaussian falloff based on arm width
            double intensity = Math.exp(-(armDist * armDist) / (2.0 * Math.pow(spiralParameters.getArmWidth(), 2)));
            maxArmIntensity = Math.max(maxArmIntensity, intensity);
        }

        return maxArmIntensity;
    }

    /**
     * Parallel batch rendering using Java 21 Virtual Threads.
     * Returns a float buffer compatible with PNG or TIFF HDR.
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
        } // Executor auto-closes, waiting for all virtual threads to complete

        log.info("Spiral Galaxy buffer generation completed ({}x{})", width, height);
        return buffer;
    }

    private double normalizeAngle(double angle) {
        while (angle > Math.PI)
            angle -= 2.0 * Math.PI;
        while (angle < -Math.PI)
            angle += 2.0 * Math.PI;
        return angle;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int width = 1000;
        private int height = 1000;
        private long seed = 12345L;
        private CoreParameters coreParameters;
        private SpiralStructureParameters spiralParameters;

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

        public Builder spiralParameters(SpiralStructureParameters sp) {
            this.spiralParameters = sp;
            return this;
        }

        public SpiralGalaxyGenerator build() {
            return new SpiralGalaxyGenerator(width, height, seed, coreParameters, spiralParameters);
        }
    }
}
