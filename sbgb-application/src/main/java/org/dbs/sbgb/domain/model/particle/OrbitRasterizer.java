package org.dbs.sbgb.domain.model.particle;

import lombok.Builder;
import org.dbs.sbgb.domain.model.densitywave.StarParticle;

@Builder
public class OrbitRasterizer implements ParticleRasterizer<StarParticle> {

    private static final int ORBIT_SAMPLES = 360;
    private static final int MAX_SEGMENT_PIXELS = 40;

    private final int centerX;
    private final int centerY;
    private final float scale;

    @Builder.Default
    private final int pertN = 0;

    @Builder.Default
    private final float pertAmp = 0.0f;

    @Override
    public void rasterize(StarParticle particle, DensityGrid grid) {
        boolean hasPerturbation = pertN > 0 && pertAmp > 0;
        float cosT = (float) Math.cos(particle.tiltAngle());
        float sinT = (float) Math.sin(particle.tiltAngle());
        float magPerSample = particle.magnitude() / ORBIT_SAMPLES;

        int prevPx = Integer.MIN_VALUE;
        int prevPy = Integer.MIN_VALUE;

        for (int i = 0; i <= ORBIT_SAMPLES; i++) {
            double theta = 2.0 * Math.PI * i / ORBIT_SAMPLES;
            float cosAlpha = (float) Math.cos(theta);
            float sinAlpha = (float) Math.sin(theta);

            float xOrbit = particle.semiMajorAxis() * cosAlpha * cosT - particle.semiMinorAxis() * sinAlpha * sinT;
            float yOrbit = particle.semiMajorAxis() * cosAlpha * sinT + particle.semiMinorAxis() * sinAlpha * cosT;

            if (hasPerturbation) {
                xOrbit += (particle.semiMajorAxis() / pertAmp) * (float) Math.sin(theta * 2.0 * pertN);
                yOrbit += (particle.semiMajorAxis() / pertAmp) * (float) Math.cos(theta * 2.0 * pertN);
            }

            int px = centerX + Math.round(xOrbit * scale);
            int py = centerY + Math.round(yOrbit * scale);

            if (prevPx != Integer.MIN_VALUE) {
                drawSegment(grid, prevPx, prevPy, px, py, magPerSample);
            }

            prevPx = px;
            prevPy = py;
        }
    }

    private void drawSegment(DensityGrid grid, int x0, int y0, int x1, int y1, float magnitude) {
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
            grid.add(x, y, magPerPixel);
        }
    }
}
