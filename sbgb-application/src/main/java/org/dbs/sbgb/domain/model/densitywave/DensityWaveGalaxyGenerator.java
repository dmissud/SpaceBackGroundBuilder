package org.dbs.sbgb.domain.model.densitywave;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DensityWaveGalaxyGenerator {

    private static final int H2_REGION_COUNT = 400;
    private static final int CDF_STEPS = 1000;
    private static final float STAR_BASE_TEMP = 6000.0f;
    private static final float STAR_TEMP_VARIANCE = 2000.0f;
    private static final float BRIGHT_STAR_FRACTION = 1.0f / 60.0f;
    private static final float DUST_MAG_MIN = 0.02f;
    private static final float DUST_MAG_MAX = 0.17f;
    private static final float DUST_TEMP_SCALE = 4.5f;
    private static final float FILAMENT_RADIAL_VARIATION = 200.0f;
    private static final float FILAMENT_ANGULAR_SCATTER = 10.0f;
    private static final float FILAMENT_TEMP_OFFSET = 1000.0f;

    private final DensityWaveGalaxyParams params;
    private final long seed;
    private final DensityWaveOrbitalMechanics mechanics;
    private final CumulativeDistributionFunction cdf;

    public DensityWaveGalaxyGenerator(DensityWaveGalaxyParams params, long seed) {
        this.params = params;
        this.seed = seed;
        this.mechanics = new DensityWaveOrbitalMechanics(
                params.galaxyRadius(), params.coreRadius(),
                params.eccentricityInner(), params.eccentricityOuter(),
                params.angleOffset());
        this.cdf = buildCdf();
    }

    public List<StarParticle> generate() {
        Random random = new Random(seed);
        List<StarParticle> particles = new ArrayList<>();
        particles.addAll(generateStars(random));
        particles.addAll(generateDust(random));
        particles.addAll(generateFilaments(random));
        particles.addAll(generateH2Regions(random));
        return particles;
    }

    private CumulativeDistributionFunction buildCdf() {
        float farField = params.galaxyRadius() * 2.0f;
        double discScale = params.galaxyRadius() / 3.0;
        CumulativeDistributionFunction distribution = new CumulativeDistributionFunction();
        distribution.setupRealistic(1.0, 0.02, discScale, params.coreRadius(), 0.0, farField, CDF_STEPS);
        return distribution;
    }

    private List<StarParticle> generateStars(Random random) {
        List<StarParticle> stars = new ArrayList<>(params.starCount());
        for (int i = 0; i < params.starCount(); i++) {
            float radius = (float) cdf.valFromProb(random.nextDouble());
            float a = radius;
            float b = a * mechanics.eccentricity(radius);
            float tilt = mechanics.angularOffset(radius);
            float theta0 = (float) (360.0 * random.nextDouble());
            float velTheta = (float) mechanics.orbitalVelocity(radius);
            float temp = STAR_BASE_TEMP + (float) (random.nextDouble() * 2 - 1) * STAR_TEMP_VARIANCE;
            float mag = random.nextFloat() < BRIGHT_STAR_FRACTION
                    ? 0.2f + random.nextFloat() * 0.4f
                    : 0.1f + random.nextFloat() * 0.4f;
            stars.add(new StarParticle(theta0, velTheta, tilt, a, b, temp, mag, StarParticleType.STAR));
        }
        return stars;
    }

    private List<StarParticle> generateDust(Random random) {
        List<StarParticle> dust = new ArrayList<>(params.starCount());
        for (int i = 0; i < params.starCount(); i++) {
            float a, b;
            if (i % 2 == 0) {
                float radius = (float) cdf.valFromProb(random.nextDouble());
                a = radius;
                b = a * mechanics.eccentricity(radius);
            } else {
                a = 1.0f + random.nextFloat() * params.galaxyRadius();
                b = 1.0f + random.nextFloat() * params.galaxyRadius();
            }
            float tilt = mechanics.angularOffset(a);
            float theta0 = (float) (360.0 * random.nextDouble());
            float velTheta = (float) mechanics.orbitalVelocity(a);
            float temp = params.baseTemp() + a / DUST_TEMP_SCALE;
            float mag = DUST_MAG_MIN + random.nextFloat() * (DUST_MAG_MAX - DUST_MAG_MIN);
            dust.add(new StarParticle(theta0, velTheta, tilt, a, b, temp, mag, StarParticleType.DUST));
        }
        return dust;
    }

    private List<StarParticle> generateFilaments(Random random) {
        int filamentCount = Math.max(1, params.starCount() / 100);
        List<StarParticle> filaments = new ArrayList<>();
        for (int f = 0; f < filamentCount; f++) {
            float originRad = (float) cdf.valFromProb(random.nextDouble());
            float originAngle = (float) (random.nextDouble() * 360.0);
            int size = 3 + random.nextInt(3);
            for (int p = 0; p < size; p++) {
                float rad = originRad + FILAMENT_RADIAL_VARIATION - 2 * FILAMENT_RADIAL_VARIATION * random.nextFloat();
                rad = Math.max(1.0f, Math.min(params.galaxyRadius() * 2.0f, rad));
                float b = rad * mechanics.eccentricity(rad);
                float tilt = originAngle + (random.nextFloat() * 2 - 1) * FILAMENT_ANGULAR_SCATTER;
                float theta0 = (float) (360.0 * random.nextDouble());
                float velTheta = (float) mechanics.orbitalVelocity(rad);
                float temp = params.baseTemp() + rad / DUST_TEMP_SCALE - FILAMENT_TEMP_OFFSET;
                float mag = DUST_MAG_MIN + random.nextFloat() * (DUST_MAG_MAX - DUST_MAG_MIN);
                filaments.add(new StarParticle(theta0, velTheta, tilt, rad, b, temp, mag, StarParticleType.DUST));
            }
        }
        return filaments;
    }

    private List<StarParticle> generateH2Regions(Random random) {
        List<StarParticle> h2 = new ArrayList<>(H2_REGION_COUNT * 2);
        for (int i = 0; i < H2_REGION_COUNT; i++) {
            float radius = (float) cdf.valFromProb(random.nextDouble());
            float a = radius;
            float b = a * mechanics.eccentricity(radius);
            float tilt = mechanics.angularOffset(radius);
            float theta0 = (float) (360.0 * random.nextDouble());
            float velTheta = (float) mechanics.orbitalVelocity(radius);
            float temp = params.baseTemp() + a / DUST_TEMP_SCALE;
            float mag = 0.1f + 0.05f * random.nextFloat();
            h2.add(new StarParticle(theta0, velTheta, tilt, a, b, temp, mag, StarParticleType.H2_OUTER));
            h2.add(new StarParticle(theta0, velTheta, tilt, a, b, temp, mag, StarParticleType.H2_CORE));
        }
        return h2;
    }
}
