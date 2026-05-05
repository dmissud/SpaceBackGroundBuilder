package org.dbs.sbgb.domain.model.particle;

import lombok.Builder;

@Builder
public class PointRasterizer implements ParticleRasterizer<StellarPoint> {

    private final int centerX;
    private final int centerY;
    private final float scale;

    @Builder.Default
    private final int splatRadius = 2;

    @Override
    public void rasterize(StellarPoint point, DensityGrid grid) {
        int px = centerX + Math.round(point.x() * scale);
        int py = centerY + Math.round(point.y() * scale);

        if (splatRadius == 0) {
            grid.add(px, py, point.magnitude());
            return;
        }

        float totalWeight = computeTotalWeight();
        for (int dx = -splatRadius; dx <= splatRadius; dx++) {
            for (int dy = -splatRadius; dy <= splatRadius; dy++) {
                float weight = gaussianWeight(dx, dy);
                grid.add(px + dx, py + dy, point.magnitude() * weight / totalWeight);
            }
        }
    }

    private float gaussianWeight(int dx, int dy) {
        float sigma = splatRadius / 2.0f;
        float dist2 = dx * dx + dy * dy;
        return (float) Math.exp(-dist2 / (2.0f * sigma * sigma));
    }

    private float computeTotalWeight() {
        float total = 0;
        for (int dx = -splatRadius; dx <= splatRadius; dx++) {
            for (int dy = -splatRadius; dy <= splatRadius; dy++) {
                total += gaussianWeight(dx, dy);
            }
        }
        return total;
    }
}
