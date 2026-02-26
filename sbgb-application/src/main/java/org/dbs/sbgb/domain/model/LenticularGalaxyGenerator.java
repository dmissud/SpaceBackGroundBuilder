package org.dbs.sbgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import de.articdive.jnoise.pipeline.JNoise;
import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.model.parameters.CoreParameters;
import org.dbs.sbgb.domain.model.parameters.LenticularShapeParameters;

import java.util.concurrent.Executors;

/**
 * Implementation of Lenticular Galaxy Generator.
 * Simulates a central bulge (Sersic) and a flatter disk component.
 */
@Slf4j
public class LenticularGalaxyGenerator extends AbstractGalaxyGenerator {

    private final LenticularShapeParameters lenticularParameters;
    private final JNoise jNoise;
    private final double orientationAngleRad;
    private final SersicProfile sersicProfile;

    public LenticularGalaxyGenerator(int width, int height,
                                     long seed,
                                     CoreParameters coreParameters,
                                     LenticularShapeParameters lenticularParameters) {
        super(width, height, null, coreParameters);
        this.lenticularParameters = lenticularParameters;
        this.orientationAngleRad = Math.toRadians(lenticularParameters.getOrientationAngle());
        this.sersicProfile = new SersicProfile(
                lenticularParameters.getSersicIndex(),
                coreParameters.getGalaxyRadius() * 0.4);

        this.jNoise = JNoise.newBuilder()
                .perlin(seed, Interpolation.COSINE, FadeFunction.CUBIC_POLY)
                .scale(0.015)
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

        // Elliptical distance for the bulge
        double bulgeDistance = Math.sqrt(rotX * rotX
                + (rotY * rotY) / (lenticularParameters.getAxisRatio() * lenticularParameters.getAxisRatio()));

        // Intensity from bulge (Sersic profile)
        double bulgeIntensity = sersicProfile.computeIntensity(bulgeDistance);

        // Simulating the disk (flatter than the bulge)
        double diskAxisRatio = Math.min(1.0, lenticularParameters.getAxisRatio() * 0.5);
        double diskDistance = Math.sqrt(rotX * rotX
                + (rotY * rotY) / (diskAxisRatio * diskAxisRatio));
        
        // Exponential profile for the disk
        double diskIntensity = Math.exp(-diskDistance / (coreParameters.getGalaxyRadius() * 0.6));

        // Combine components
        double combinedIntensity = (1.0 - lenticularParameters.getDiskContribution()) * bulgeIntensity 
                                  + lenticularParameters.getDiskContribution() * diskIntensity;

        // Noise Modulation
        double perlinNoise = jNoise.evaluateNoise(x, y);
        double finalIntensity = (0.3 + 0.7 * perlinNoise) * combinedIntensity;

        return Math.clamp(finalIntensity, 0.0, 1.0);
    }

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
        log.info("Lenticular Galaxy buffer generation completed ({}x{})", width, height);
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
        private LenticularShapeParameters lenticularParameters;

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

        public Builder lenticularParameters(LenticularShapeParameters lp) {
            this.lenticularParameters = lp;
            return this;
        }

        public LenticularGalaxyGenerator build() {
            return new LenticularGalaxyGenerator(width, height, seed, coreParameters, lenticularParameters);
        }
    }
}
