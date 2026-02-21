package org.dbs.sbgb.domain.model;

import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.constant.CoreIntensityConstants;
import org.dbs.sbgb.domain.constant.RadialFalloffConstants;
import org.dbs.sbgb.domain.model.parameters.CoreParameters;
import org.dbs.sbgb.domain.model.parameters.IrregularStructureParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class IrregularGalaxyGenerator extends AbstractGalaxyGenerator {

    private final IrregularStructureParameters irregularParameters;
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
            CoreParameters coreParameters,
            IrregularStructureParameters irregularParameters) {
        super(width, height, noiseGenerator, coreParameters);
        this.irregularParameters = irregularParameters;

        // Generate random clumps of star formation
        this.clumps = generateClumps(seed, irregularParameters.getClumpCount(), irregularParameters.getClumpSize());
    }

    private List<Clump> generateClumps(long seed, int clumpCount, double clumpSize) {
        List<Clump> clumps = new ArrayList<>();
        Random random = new Random(seed);

        for (int i = 0; i < clumpCount; i++) {
            // Distribute clumps randomly within galaxy radius
            double angle = random.nextDouble() * 2.0 * Math.PI;
            double distance = Math.sqrt(random.nextDouble()) * coreParameters.getGalaxyRadius() * 0.8; // 80% of radius

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
        PixelGeometry geo = calculateGeometry(x, y);
        if (geo == null)
            return 0.0;

        // Core contribution (small for irregular galaxies)
        double coreRadius = coreParameters.getGalaxyRadius() * coreParameters.getCoreSize();
        double coreIntensity = Math.exp(-(geo.distance * geo.distance)
                / (RadialFalloffConstants.GAUSSIAN_DENOMINATOR * coreRadius * coreRadius));
        coreIntensity *= CoreIntensityConstants.IRREGULAR_CORE_MULTIPLIER;

        // Clump contributions (Gaussian profiles)
        double clumpIntensity = 0.0;
        for (Clump clump : clumps) {
            double clumpDx = x - clump.x;
            double clumpDy = y - clump.y;
            double clumpDist = Math.sqrt(clumpDx * clumpDx + clumpDy * clumpDy);
            double clumpProfile = Math.exp(-(clumpDist * clumpDist)
                    / (RadialFalloffConstants.GAUSSIAN_DENOMINATOR * clump.size * clump.size));
            clumpIntensity += clumpProfile * clump.intensity;
        }
        clumpIntensity = Math.min(clumpIntensity, 1.0);

        // Strong Perlin noise for irregularity
        double noiseValue = noiseGenerator.scaleNoiseNormalizedValue(x, y);
        double noiseFactor = (1.0 - irregularParameters.getIrregularity())
                + (noiseValue * irregularParameters.getIrregularity());

        // Combine all sources
        double baseIntensity = coreIntensity + clumpIntensity * CoreIntensityConstants.IRREGULAR_CLUMP_WEIGHT;

        // Soft radial falloff
        double radialFalloff = Math.pow(1.0 - geo.normalizedDistance, 1.5);

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
        private CoreParameters coreParameters = CoreParameters.builder()
                .coreSize(0.05)
                .galaxyRadius(1500.0)
                .build();
        private IrregularStructureParameters irregularParameters = IrregularStructureParameters.builder()
                .irregularity(0.8)
                .clumpCount(15)
                .clumpSize(80.0)
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

        public Builder irregularParameters(IrregularStructureParameters irregularParameters) {
            this.irregularParameters = irregularParameters;
            return this;
        }

        public IrregularGalaxyGenerator build() {
            if (noiseGenerator == null) {
                throw new IllegalStateException("noiseGenerator must be set");
            }
            return new IrregularGalaxyGenerator(width, height, noiseGenerator, seed, coreParameters,
                    irregularParameters);
        }
    }
}
