package org.dbs.sbgb.domain.model.densitywave;

import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;

import java.util.List;

@Slf4j
public class DensityWaveIntensityCalculator implements GalaxyIntensityCalculator {

    private static final int ORBIT_SAMPLES = 360;
    private static final float ZOOM_FACTOR = 0.5f;
    private static final float GAMMA = 0.45f;
    private static final int GLOW_BLUR_PASSES = 3;
    private static final int MAX_SEGMENT_PIXELS = 40;

    private final float[][] densityGrid;
    private final int width;
    private final int height;

    public DensityWaveIntensityCalculator(int width, int height, long seed, DensityWaveGalaxyParams params) {
        this.width = width;
        this.height = height;
        this.densityGrid = new float[width][height];

        log.info("[DensityWave] Generating {} particles (seed={}, {}x{})", params.starCount(), seed, width, height);
        List<StarParticle> particles = new DensityWaveGalaxyGenerator(params, seed).generate();
        log.info("[DensityWave] {} particles generated — rendering orbits ({} samples each)...", particles.size(), ORBIT_SAMPLES);

        float scale = computeScale(width, height, params.galaxyRadius());
        renderOrbits(particles, scale, params.pertN(), params.pertAmp());
        log.info("[DensityWave] Orbit rendering done — applying box blur ({} passes)...", GLOW_BLUR_PASSES);

        applyBoxBlur(computeBlurRadius(width));
        log.info("[DensityWave] Blur done — normalizing and applying gamma {}...", GAMMA);

        normalize();
        applyGamma();
        log.info("[DensityWave] Done.");
    }

    @Override
    public double calculateGalaxyIntensity(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return 0.0;
        return densityGrid[x][y];
    }

    private float computeScale(int width, int height, float galaxyRadius) {
        return Math.min(width, height) / (2.0f * galaxyRadius * ZOOM_FACTOR);
    }

    private int computeBlurRadius(int width) {
        return Math.max(3, width / 500);
    }

    private void renderOrbits(List<StarParticle> particles, float scale, int pertN, float pertAmp) {
        int cx = width / 2;
        int cy = height / 2;
        boolean hasPerturbation = pertN > 0 && pertAmp > 0;

        for (StarParticle p : particles) {
            float cosT = (float) Math.cos(p.tiltAngle());
            float sinT = (float) Math.sin(p.tiltAngle());
            float magPerSample = p.magnitude() / ORBIT_SAMPLES;

            int prevPx = Integer.MIN_VALUE;
            int prevPy = Integer.MIN_VALUE;

            for (int i = 0; i <= ORBIT_SAMPLES; i++) {
                double theta = 2.0 * Math.PI * i / ORBIT_SAMPLES;
                float cosAlpha = (float) Math.cos(theta);
                float sinAlpha = (float) Math.sin(theta);

                float xOrbit = p.semiMajorAxis() * cosAlpha * cosT - p.semiMinorAxis() * sinAlpha * sinT;
                float yOrbit = p.semiMajorAxis() * cosAlpha * sinT + p.semiMinorAxis() * sinAlpha * cosT;

                if (hasPerturbation) {
                    xOrbit += (p.semiMajorAxis() / pertAmp) * (float) Math.sin(theta * 2.0 * pertN);
                    yOrbit += (p.semiMajorAxis() / pertAmp) * (float) Math.cos(theta * 2.0 * pertN);
                }

                int px = cx + Math.round(xOrbit * scale);
                int py = cy + Math.round(yOrbit * scale);

                if (prevPx != Integer.MIN_VALUE) {
                    drawSegment(prevPx, prevPy, px, py, magPerSample);
                }

                prevPx = px;
                prevPy = py;
            }
        }
    }

    private void drawSegment(int x0, int y0, int x1, int y1, float magnitude) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int steps = Math.max(1, Math.max(dx, dy));
        if (steps > MAX_SEGMENT_PIXELS) return;

        float magPerPixel = magnitude / steps;
        float xStep = (float) (x1 - x0) / steps;
        float yStep = (float) (y1 - y0) / steps;

        for (int i = 0; i <= steps; i++) {
            int x = Math.round(x0 + i * xStep);
            int y = Math.round(y0 + i * yStep);
            if (x >= 0 && x < width && y >= 0 && y < height) {
                densityGrid[x][y] += magPerPixel;
            }
        }
    }

    private void applyBoxBlur(int radius) {
        float[] row = new float[Math.max(width, height)];
        for (int pass = 0; pass < GLOW_BLUR_PASSES; pass++) {
            // Horizontal pass
            for (int y = 0; y < height; y++) {
                blurRow(densityGrid, y, row, width, radius, true);
            }
            // Vertical pass
            for (int x = 0; x < width; x++) {
                blurRow(densityGrid, x, row, height, radius, false);
            }
        }
    }

    private void blurRow(float[][] grid, int line, float[] tmp, int len, int radius, boolean horizontal) {
        double sum = 0;
        int count = 0;
        for (int i = 0; i < Math.min(radius, len); i++) {
            sum += horizontal ? grid[i][line] : grid[line][i];
            count++;
        }
        for (int i = 0; i < len; i++) {
            int add = i + radius;
            int remove = i - radius - 1;
            if (add < len) { sum += horizontal ? grid[add][line] : grid[line][add]; count++; }
            if (remove >= 0) { sum -= horizontal ? grid[remove][line] : grid[line][remove]; count--; }
            tmp[i] = (float) (sum / count);
        }
        for (int i = 0; i < len; i++) {
            if (horizontal) grid[i][line] = tmp[i];
            else grid[line][i] = tmp[i];
        }
    }

    private void normalize() {
        float max = 0.0f;
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                if (densityGrid[x][y] > max) max = densityGrid[x][y];
        if (max == 0.0f) return;
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                densityGrid[x][y] /= max;
    }

    private void applyGamma() {
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                densityGrid[x][y] = (float) Math.pow(densityGrid[x][y], GAMMA);
    }
}
