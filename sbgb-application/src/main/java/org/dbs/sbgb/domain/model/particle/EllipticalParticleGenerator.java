package org.dbs.sbgb.domain.model.particle;

import org.dbs.sbgb.domain.model.densitywave.CumulativeDistributionFunction;
import org.dbs.sbgb.domain.model.parameters.EllipticalShapeParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EllipticalParticleGenerator {

    private static final int CDF_STEPS = 1000;
    private static final float CORE_TEMP = 9000f;
    private static final float OUTER_TEMP = 3000f;
    private static final float MAG_MIN = 0.05f;
    private static final float MAG_MAX = 0.4f;

    private final float galaxyRadius;
    private final int starCount;
    private final EllipticalShapeParameters shape;
    private final long seed;
    private final CumulativeDistributionFunction cdf;

    public EllipticalParticleGenerator(float galaxyRadius, int starCount,
                                       EllipticalShapeParameters shape, long seed) {
        this.galaxyRadius = galaxyRadius;
        this.starCount = starCount;
        this.shape = shape;
        this.seed = seed;
        this.cdf = buildCdf();
    }

    public List<StellarPoint> generate() {
        Random random = new Random(seed);
        List<StellarPoint> points = new ArrayList<>(starCount);
        double orientationRad = Math.toRadians(shape.getOrientationAngle());
        double cosO = Math.cos(orientationRad);
        double sinO = Math.sin(orientationRad);

        for (int i = 0; i < starCount; i++) {
            float radius = (float) cdf.valFromProb(random.nextDouble());
            double angle = 2.0 * Math.PI * random.nextDouble();

            float xRaw = radius * (float) Math.cos(angle);
            float yRaw = radius * (float) Math.sin(angle) * (float) shape.getAxisRatio();

            float x = (float) (xRaw * cosO - yRaw * sinO);
            float y = (float) (xRaw * sinO + yRaw * cosO);

            float temp = temperatureAt(radius);
            float mag = MAG_MIN + random.nextFloat() * (MAG_MAX - MAG_MIN);
            points.add(new StellarPoint(x, y, mag, temp));
        }
        return points;
    }

    private CumulativeDistributionFunction buildCdf() {
        double coreRadius = galaxyRadius * 0.1;
        double discScale = galaxyRadius * 0.4;
        CumulativeDistributionFunction distribution = new CumulativeDistributionFunction();
        distribution.setupRealistic(1.0, 0.01, discScale, coreRadius, 0.0, galaxyRadius * 2.5, CDF_STEPS);
        return distribution;
    }

    private float temperatureAt(float radius) {
        float fraction = Math.min(1.0f, radius / galaxyRadius);
        return CORE_TEMP - (CORE_TEMP - OUTER_TEMP) * fraction;
    }
}
