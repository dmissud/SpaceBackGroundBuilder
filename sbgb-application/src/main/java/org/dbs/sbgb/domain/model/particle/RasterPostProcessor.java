package org.dbs.sbgb.domain.model.particle;

import lombok.Builder;

@Builder
public class RasterPostProcessor {

    @Builder.Default
    private final int blurPasses = 3;

    @Builder.Default
    private final int blurRadius = 3;

    @Builder.Default
    private final float gamma = 0.45f;

    public void apply(DensityGrid grid) {
        if (blurPasses > 0 && blurRadius > 0) {
            applyBoxBlur(grid);
        }
        normalize(grid);
        applyGamma(grid);
    }

    private void applyBoxBlur(DensityGrid grid) {
        int width = grid.width();
        int height = grid.height();
        float[][] cells = grid.raw();
        float[] tmp = new float[Math.max(width, height)];
        for (int pass = 0; pass < blurPasses; pass++) {
            for (int y = 0; y < height; y++) {
                blurLine(cells, y, tmp, width, true);
            }
            for (int x = 0; x < width; x++) {
                blurLine(cells, x, tmp, height, false);
            }
        }
    }

    private void blurLine(float[][] cells, int line, float[] tmp, int len, boolean horizontal) {
        double sum = 0;
        int count = 0;
        for (int i = 0; i < Math.min(blurRadius, len); i++) {
            sum += horizontal ? cells[i][line] : cells[line][i];
            count++;
        }
        for (int i = 0; i < len; i++) {
            int add = i + blurRadius;
            int remove = i - blurRadius - 1;
            if (add < len) {
                sum += horizontal ? cells[add][line] : cells[line][add];
                count++;
            }
            if (remove >= 0) {
                sum -= horizontal ? cells[remove][line] : cells[line][remove];
                count--;
            }
            tmp[i] = (float) (sum / count);
        }
        for (int i = 0; i < len; i++) {
            if (horizontal) cells[i][line] = tmp[i];
            else cells[line][i] = tmp[i];
        }
    }

    private void normalize(DensityGrid grid) {
        float max = grid.max();
        if (max == 0.0f) return;
        float[][] cells = grid.raw();
        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                cells[x][y] /= max;
            }
        }
    }

    private void applyGamma(DensityGrid grid) {
        float[][] cells = grid.raw();
        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                cells[x][y] = (float) Math.pow(cells[x][y], gamma);
            }
        }
    }
}
