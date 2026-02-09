package org.dbs.sbgb.domain.model;

import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.constant.NoiseModulationConstants;
import org.dbs.sbgb.domain.constant.RadialFalloffConstants;
import org.dbs.sbgb.domain.model.parameters.CoreParameters;
import org.dbs.sbgb.domain.model.parameters.RingStructureParameters;

@Slf4j
public class RingGalaxyGenerator implements GalaxyIntensityCalculator {

    private final int width;
    private final int height;
    private final double centerX;
    private final double centerY;
    private final PerlinGenerator noiseGenerator;
    private final CoreParameters coreParameters;
    private final RingStructureParameters ringParameters;

    private RingGalaxyGenerator(int width, int height,
                                PerlinGenerator noiseGenerator,
                                CoreParameters coreParameters,
                                RingStructureParameters ringParameters) {
        this.width = width;
        this.height = height;
        this.centerX = width / 2.0;
        this.centerY = height / 2.0;
        this.noiseGenerator = noiseGenerator;
        this.coreParameters = coreParameters;
        this.ringParameters = ringParameters;
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

        // Core contribution (Gaussian)
        double coreRadius = coreParameters.getGalaxyRadius() * coreParameters.getCoreSize();
        double coreIntensity = Math.exp(-(distance * distance) / (2.0 * coreRadius * coreRadius));
        coreIntensity *= ringParameters.getCoreToRingRatio();

        // Ring contribution (Gaussian profile centered on ringRadius)
        double ringDistance = Math.abs(distance - ringParameters.getRingRadius());
        double ringProfile = Math.exp(-(ringDistance * ringDistance) / (2.0 * ringParameters.getRingWidth() * ringParameters.getRingWidth()));
        double ringContribution = ringProfile * ringParameters.getRingIntensity();

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
        private CoreParameters coreParameters = CoreParameters.builder()
                .coreSize(0.05)
                .galaxyRadius(1500.0)
                .build();
        private RingStructureParameters ringParameters = RingStructureParameters.builder()
                .ringRadius(900.0)
                .ringWidth(150.0)
                .ringIntensity(1.0)
                .coreToRingRatio(0.3)
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

        public Builder coreParameters(CoreParameters coreParameters) {
            this.coreParameters = coreParameters;
            return this;
        }

        public Builder ringParameters(RingStructureParameters ringParameters) {
            this.ringParameters = ringParameters;
            return this;
        }

        public RingGalaxyGenerator build() {
            if (noiseGenerator == null) {
                throw new IllegalStateException("noiseGenerator must be set");
            }
            return new RingGalaxyGenerator(width, height, noiseGenerator, coreParameters, ringParameters);
        }
    }
}
