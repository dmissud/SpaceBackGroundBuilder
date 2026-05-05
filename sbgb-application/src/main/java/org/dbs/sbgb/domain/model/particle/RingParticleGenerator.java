package org.dbs.sbgb.domain.model.particle;

import org.dbs.sbgb.domain.model.parameters.RingStructureParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RingParticleGenerator {

    private static final float RING_STAR_TEMP = 8000f;
    private static final float DUST_TEMP = 2500f;
    private static final float H2_TEMP = 14000f;
    private static final float CORE_TEMP = 5500f;
    private static final int H2_CLUSTER_COUNT = 30;
    private static final int H2_CLUSTER_SIZE = 15;

    private final float galaxyRadius;
    private final RingStructureParameters params;
    private final int totalCount;
    private final long seed;

    public RingParticleGenerator(float galaxyRadius, RingStructureParameters params,
                                 int totalCount, long seed) {
        this.galaxyRadius = galaxyRadius;
        this.params = params;
        this.totalCount = totalCount;
        this.seed = seed;
    }

    public List<StellarPoint> generate() {
        Random random = new Random(seed);
        List<StellarPoint> points = new ArrayList<>(totalCount);

        float ringRadius = (float) params.getRingRadius();
        float ringWidth = (float) params.getRingWidth();
        float ringIntensity = (float) params.getRingIntensity();
        float coreRatio = (float) params.getCoreToRingRatio();

        int coreCount = (int) (totalCount * coreRatio * 0.15f);
        int dustCount = (int) (totalCount * 0.25f);
        int starCount = totalCount - coreCount - dustCount - H2_CLUSTER_COUNT * H2_CLUSTER_SIZE;

        points.addAll(generateRingStars(random, starCount, ringRadius, ringWidth, ringIntensity));
        points.addAll(generateDust(random, dustCount, ringRadius, ringWidth));
        points.addAll(generateH2Clusters(random, ringRadius, ringWidth));
        points.addAll(generateCore(random, coreCount));

        return points;
    }

    private List<StellarPoint> generateRingStars(Random random, int count,
                                                  float ringRadius, float ringWidth, float ringIntensity) {
        List<StellarPoint> stars = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            float r = ringRadius + (float) (random.nextGaussian() * ringWidth);
            double angle = 2.0 * Math.PI * random.nextDouble();
            float x = r * (float) Math.cos(angle);
            float y = r * (float) Math.sin(angle);
            float mag = 0.05f + random.nextFloat() * 0.25f * ringIntensity;
            float temp = RING_STAR_TEMP + (random.nextFloat() - 0.5f) * 2000f;
            stars.add(new StellarPoint(x, y, mag, temp));
        }
        return stars;
    }

    private List<StellarPoint> generateDust(Random random, int count,
                                             float ringRadius, float ringWidth) {
        List<StellarPoint> dust = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            float r = ringRadius + (float) (random.nextGaussian() * ringWidth * 1.5f);
            double angle = 2.0 * Math.PI * random.nextDouble();
            float x = r * (float) Math.cos(angle);
            float y = r * (float) Math.sin(angle);
            float mag = 0.01f + random.nextFloat() * 0.06f;
            dust.add(new StellarPoint(x, y, mag, DUST_TEMP));
        }
        return dust;
    }

    private List<StellarPoint> generateH2Clusters(Random random, float ringRadius, float ringWidth) {
        List<StellarPoint> h2 = new ArrayList<>(H2_CLUSTER_COUNT * H2_CLUSTER_SIZE);
        for (int c = 0; c < H2_CLUSTER_COUNT; c++) {
            double clusterAngle = 2.0 * Math.PI * random.nextDouble();
            float clusterR = ringRadius + (float) (random.nextGaussian() * ringWidth * 0.5f);
            float cx = clusterR * (float) Math.cos(clusterAngle);
            float cy = clusterR * (float) Math.sin(clusterAngle);
            for (int p = 0; p < H2_CLUSTER_SIZE; p++) {
                float spread = ringWidth * 0.1f;
                float x = cx + (float) (random.nextGaussian() * spread);
                float y = cy + (float) (random.nextGaussian() * spread);
                float mag = 0.15f + random.nextFloat() * 0.35f;
                h2.add(new StellarPoint(x, y, mag, H2_TEMP));
            }
        }
        return h2;
    }

    private List<StellarPoint> generateCore(Random random, int count) {
        List<StellarPoint> core = new ArrayList<>(count);
        float coreRadius = galaxyRadius * 0.05f;
        for (int i = 0; i < count; i++) {
            float x = (float) (random.nextGaussian() * coreRadius);
            float y = (float) (random.nextGaussian() * coreRadius);
            float mag = 0.1f + random.nextFloat() * 0.4f;
            core.add(new StellarPoint(x, y, mag, CORE_TEMP));
        }
        return core;
    }
}
