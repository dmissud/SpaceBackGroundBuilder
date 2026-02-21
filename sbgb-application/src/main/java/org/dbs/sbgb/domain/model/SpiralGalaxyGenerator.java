package org.dbs.sbgb.domain.model;

import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.constant.CoreIntensityConstants;
import org.dbs.sbgb.domain.constant.NoiseModulationConstants;
import org.dbs.sbgb.domain.constant.RadialFalloffConstants;
import org.dbs.sbgb.domain.model.parameters.CoreParameters;
import org.dbs.sbgb.domain.model.parameters.SpiralStructureParameters;

/**
 * Generator for realistic galaxy structures with spiral arms
 * Combines geometric calculations (radial distance, spiral rotation)
 * with Perlin noise for organic appearance
 */
@Slf4j
public class SpiralGalaxyGenerator extends AbstractGalaxyGenerator {

    // Galaxy parameters (Value Objects)
    private final SpiralStructureParameters spiralParameters;

    public SpiralGalaxyGenerator(int width, int height,
            PerlinGenerator noiseGenerator,
            CoreParameters coreParameters,
            SpiralStructureParameters spiralParameters) {
        super(width, height, noiseGenerator, coreParameters);
        this.spiralParameters = spiralParameters;
    }

    @Override
    public double calculateGalaxyIntensity(int x, int y) {
        PixelGeometry geo = calculateGeometry(x, y);
        if (geo == null)
            return 0.0;

        // Calculate angle from center
        double angle = Math.atan2(geo.dy, geo.dx);

        // Calculate core intensity (bright center, exponential falloff)
        double coreIntensity = calculateCoreIntensity(geo.normalizedDistance);

        // Calculate spiral arm intensity
        double armIntensity = calculateSpiralArmIntensity(angle, geo.normalizedDistance);

        // Add noise for organic look
        double noiseValue = noiseGenerator.scaleNoiseNormalizedValue(x, y);
        double noiseFactor = NoiseModulationConstants.NOISE_BASE_CONTRIBUTION
                + (noiseValue * NoiseModulationConstants.NOISE_MODULATION_RANGE);

        // Combine core and arms with radial falloff
        double radialFalloff = Math.pow(1.0 - geo.normalizedDistance, RadialFalloffConstants.STANDARD_FALLOFF_EXPONENT);
        double combinedIntensity = (coreIntensity + armIntensity) * radialFalloff * noiseFactor;

        return Math.clamp(combinedIntensity, 0.0, 1.0);
    }

    /**
     * Calculate core intensity - exponential bright center
     */
    private double calculateCoreIntensity(double normalizedDistance) {
        if (normalizedDistance < coreParameters.getCoreSize()) {
            // Very bright core with exponential falloff
            double coreDistance = normalizedDistance / coreParameters.getCoreSize();
            return Math.exp(-coreDistance * CoreIntensityConstants.CORE_EXPONENTIAL_FALLOFF)
                    * CoreIntensityConstants.CORE_BRIGHTNESS_MULTIPLIER;
        }
        return 0.0;
    }

    /**
     * Calculate spiral arm intensity using logarithmic spiral formula
     */
    private double calculateSpiralArmIntensity(double angle, double normalizedDistance) {
        double maxArmIntensity = 0.0;

        // Check each spiral arm
        for (int arm = 0; arm < spiralParameters.getNumberOfArms(); arm++) {
            double armBaseAngle = (2.0 * Math.PI * arm) / spiralParameters.getNumberOfArms();

            // Logarithmic spiral: angle = armRotation * ln(distance)
            double spiralAngle = armBaseAngle + spiralParameters.getArmRotation() * Math.log(normalizedDistance + 0.1);

            // Normalize angles to [-PI, PI]
            double angleDiff = normalizeAngle(angle - spiralAngle);

            // Calculate distance to spiral arm center
            double armDistance = Math.abs(angleDiff) * normalizedDistance * coreParameters.getGalaxyRadius()
                    / spiralParameters.getArmWidth();

            // Gaussian falloff from arm center
            double armIntensity = Math.exp(-armDistance * armDistance * 0.5);

            maxArmIntensity = Math.max(maxArmIntensity, armIntensity);
        }

        return maxArmIntensity;
    }

    /**
     * Normalize angle to [-PI, PI] range
     */
    private double normalizeAngle(double angle) {
        while (angle > Math.PI)
            angle -= 2.0 * Math.PI;
        while (angle < -Math.PI)
            angle += 2.0 * Math.PI;
        return angle;
    }

    /**
     * Create a builder for SpiralGalaxyGenerator
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int width = 4000;
        private int height = 4000;
        private PerlinGenerator noiseGenerator;
        private CoreParameters coreParameters = CoreParameters.builder()
                .coreSize(0.05)
                .galaxyRadius(1000.0)
                .build();
        private SpiralStructureParameters spiralParameters = SpiralStructureParameters.builder()
                .numberOfArms(2)
                .armWidth(80.0)
                .armRotation(4.0)
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

        public Builder spiralParameters(SpiralStructureParameters spiralParameters) {
            this.spiralParameters = spiralParameters;
            return this;
        }

        public SpiralGalaxyGenerator build() {
            if (noiseGenerator == null) {
                throw new IllegalStateException("noiseGenerator must be set");
            }
            return new SpiralGalaxyGenerator(width, height, noiseGenerator, coreParameters, spiralParameters);
        }
    }
}
