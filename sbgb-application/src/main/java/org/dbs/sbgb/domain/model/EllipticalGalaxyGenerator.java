package org.dbs.sbgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import de.articdive.jnoise.pipeline.JNoise;
import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.constant.NoiseModulationConstants;
import org.dbs.sbgb.domain.model.parameters.CoreParameters;
import org.dbs.sbgb.domain.model.parameters.EllipticalShapeParameters;

import java.util.concurrent.Executors;

/**
 * Implementation of Elliptical Galaxy Generator using JNoise 4.1.0 and Virtual
 * Threads.
 * Uses Sérsic profile with Ciotti approximation.
 */
@Slf4j
public class EllipticalGalaxyGenerator extends AbstractGalaxyGenerator {

    private final EllipticalShapeParameters ellipticalParameters;
    private final JNoise jNoise;
    private final double orientationAngleRad;
    private final double effectiveRadius;
    private final double bn;

    public EllipticalGalaxyGenerator(int width, int height,
                                     long seed,
            CoreParameters coreParameters,
            EllipticalShapeParameters ellipticalParameters) {
        super(width, height, null, coreParameters);
        this.ellipticalParameters = ellipticalParameters;
        this.orientationAngleRad = Math.toRadians(ellipticalParameters.getOrientationAngle());
        this.effectiveRadius = coreParameters.getGalaxyRadius() * 0.5;

        // Ciotti approximation: bn ≈ 2n - 1/3
        this.bn = 2.0 * ellipticalParameters.getSersicIndex() - (1.0 / 3.0);

        // Initialize JNoise 4.1.0 pipeline
        this.jNoise = JNoise.newBuilder()
                .perlin(seed, Interpolation.COSINE, FadeFunction.CUBIC_POLY)
                .scale(0.01)
                .build();
    }

    @Override
    public double calculateGalaxyIntensity(int x, int y) {
        PixelGeometry geo = calculateGeometry(x, y);
        if (geo == null)
            return 0.0;

        // Rotation for ellipse orientation
        double cosA = Math.cos(orientationAngleRad);
        double sinA = Math.sin(orientationAngleRad);
        double rotX = geo.dx * cosA + geo.dy * sinA;
        double rotY = -geo.dx * sinA + geo.dy * cosA;

        // Elliptical distance
        double ellipticalDistance = Math.sqrt(rotX * rotX
                + (rotY * rotY) / (ellipticalParameters.getAxisRatio() * ellipticalParameters.getAxisRatio()));

        // Sersic profile: I(r) = Ie * exp(-bn * ((r/re)^(1/n) - 1))
        double rRatio = ellipticalDistance / effectiveRadius;
        double sersicIntensity = Math.exp(-bn * (Math.pow(rRatio, 1.0 / ellipticalParameters.getSersicIndex()) - 1.0));

        // Noise Modulation
        double perlinNoise = jNoise.evaluateNoise(x, y);
        double finalIntensity = (0.2 + 0.8 * perlinNoise) * sersicIntensity;

        return Math.clamp(finalIntensity, 0.0, 1.0);
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
        log.info("Elliptical Galaxy buffer generation completed ({}x{})", width, height);
        return buffer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int width = 1000;
        private int height = 1000;
        private long seed = 12345L;
        private CoreParameters coreParameters;
        private EllipticalShapeParameters ellipticalParameters;

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

        public Builder ellipticalParameters(EllipticalShapeParameters ep) {
            this.ellipticalParameters = ep;
            return this;
        }

        public EllipticalGalaxyGenerator build() {
            return new EllipticalGalaxyGenerator(width, height, seed, coreParameters, ellipticalParameters);
        }
    }
}
