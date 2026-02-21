package org.dbs.sbgb.domain.model;

import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.constant.NoiseModulationConstants;
import org.dbs.sbgb.domain.model.parameters.CoreParameters;
import org.dbs.sbgb.domain.model.parameters.EllipticalShapeParameters;

@Slf4j
public class EllipticalGalaxyGenerator extends AbstractGalaxyGenerator {

    private final EllipticalShapeParameters ellipticalParameters;
    private final double orientationAngleRad;
    private final double effectiveRadius;
    private final double bn;

    private EllipticalGalaxyGenerator(int width, int height,
            PerlinGenerator noiseGenerator,
            CoreParameters coreParameters,
            EllipticalShapeParameters ellipticalParameters) {
        super(width, height, noiseGenerator, coreParameters);
        this.ellipticalParameters = ellipticalParameters;
        this.orientationAngleRad = Math.toRadians(ellipticalParameters.getOrientationAngle());
        this.effectiveRadius = coreParameters.getGalaxyRadius() * 0.5;
        this.bn = 1.9992 * ellipticalParameters.getSersicIndex() - 0.3271;
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

        // This is the normalized distance *along the ellipse*, not the regular radius
        double normalizedEllipticalDistance = ellipticalDistance / coreParameters.getGalaxyRadius();

        if (normalizedEllipticalDistance > 1.0) {
            return 0.0;
        }

        // Sersic profile
        double rRatio = ellipticalDistance / effectiveRadius;
        double sersicIntensity = Math.exp(-bn * (Math.pow(rRatio, 1.0 / ellipticalParameters.getSersicIndex()) - 1.0));

        // Perlin noise (subtle for ellipticals)
        double noiseValue = noiseGenerator.scaleNoiseNormalizedValue(x, y);
        double noiseFactor = NoiseModulationConstants.ELLIPTICAL_NOISE_BASE
                + (noiseValue * NoiseModulationConstants.ELLIPTICAL_NOISE_RANGE);

        // Smooth radial falloff
        double radialFalloff = Math.pow(1.0 - normalizedEllipticalDistance, 1.5);

        double combined = sersicIntensity * radialFalloff * noiseFactor;
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
        private EllipticalShapeParameters ellipticalParameters = EllipticalShapeParameters.builder()
                .sersicIndex(4.0)
                .axisRatio(0.7)
                .orientationAngle(0.0)
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

        public Builder ellipticalParameters(EllipticalShapeParameters ellipticalParameters) {
            this.ellipticalParameters = ellipticalParameters;
            return this;
        }

        public EllipticalGalaxyGenerator build() {
            if (noiseGenerator == null) {
                throw new IllegalStateException("noiseGenerator must be set");
            }
            return new EllipticalGalaxyGenerator(width, height, noiseGenerator, coreParameters, ellipticalParameters);
        }
    }
}
