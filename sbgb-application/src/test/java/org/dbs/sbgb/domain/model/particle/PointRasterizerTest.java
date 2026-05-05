package org.dbs.sbgb.domain.model.particle;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PointRasterizerTest {

    private static final int SIZE = 101;
    private static final int CENTER = SIZE / 2;

    @Test
    void rasterize_marks_pixel_at_projected_position() {
        DensityGrid grid = new DensityGrid(SIZE, SIZE);
        PointRasterizer rasterizer = PointRasterizer.builder()
                .centerX(CENTER).centerY(CENTER).scale(1.0f).build();
        StellarPoint point = new StellarPoint(10f, 0f, 1.0f, 5000f);

        rasterizer.rasterize(point, grid);

        float total = totalIntensity(grid, CENTER + 10, CENTER, 2);
        assertThat(total).isGreaterThan(0.0f);
    }

    @Test
    void rasterize_center_point_marks_center_pixel() {
        DensityGrid grid = new DensityGrid(SIZE, SIZE);
        PointRasterizer rasterizer = PointRasterizer.builder()
                .centerX(CENTER).centerY(CENTER).scale(1.0f).build();
        StellarPoint origin = new StellarPoint(0f, 0f, 1.0f, 5000f);

        rasterizer.rasterize(origin, grid);

        assertThat(grid.get(CENTER, CENTER)).isGreaterThan(0.0f);
    }

    @Test
    void magnitude_is_distributed_in_neighborhood() {
        DensityGrid grid = new DensityGrid(SIZE, SIZE);
        PointRasterizer rasterizer = PointRasterizer.builder()
                .centerX(CENTER).centerY(CENTER).scale(1.0f).splatRadius(2).build();
        StellarPoint point = new StellarPoint(0f, 0f, 1.0f, 5000f);

        rasterizer.rasterize(point, grid);

        assertThat(grid.get(CENTER - 1, CENTER)).isGreaterThan(0.0f);
        assertThat(grid.get(CENTER + 1, CENTER)).isGreaterThan(0.0f);
        assertThat(grid.get(CENTER, CENTER - 1)).isGreaterThan(0.0f);
        assertThat(grid.get(CENTER, CENTER + 1)).isGreaterThan(0.0f);
    }

    @Test
    void scale_factor_projects_correctly() {
        DensityGrid grid = new DensityGrid(SIZE, SIZE);
        PointRasterizer rasterizer = PointRasterizer.builder()
                .centerX(CENTER).centerY(CENTER).scale(2.0f).splatRadius(0).build();
        StellarPoint point = new StellarPoint(5f, 0f, 1.0f, 5000f);

        rasterizer.rasterize(point, grid);

        assertThat(grid.get(CENTER + 10, CENTER)).isGreaterThan(0.0f);
    }

    @Test
    void rasterize_outside_bounds_does_not_throw() {
        DensityGrid grid = new DensityGrid(SIZE, SIZE);
        PointRasterizer rasterizer = PointRasterizer.builder()
                .centerX(CENTER).centerY(CENTER).scale(1.0f).build();
        StellarPoint far = new StellarPoint(10000f, 10000f, 1.0f, 5000f);

        rasterizer.rasterize(far, grid);

        assertThat(grid.max()).isZero();
    }

    private float totalIntensity(DensityGrid grid, int cx, int cy, int radius) {
        float sum = 0;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                sum += grid.get(cx + dx, cy + dy);
            }
        }
        return sum;
    }
}
