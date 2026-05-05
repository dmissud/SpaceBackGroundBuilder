package org.dbs.sbgb.domain.model.particle;

import org.dbs.sbgb.domain.model.densitywave.StarParticle;
import org.dbs.sbgb.domain.model.densitywave.StarParticleType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrbitRasterizerTest {

    private static final int SIZE = 101;
    private static final int CENTER = SIZE / 2;

    @Test
    void rasterize_circular_orbit_marks_ring_around_center() {
        DensityGrid grid = new DensityGrid(SIZE, SIZE);
        OrbitRasterizer rasterizer = OrbitRasterizer.builder()
                .centerX(CENTER).centerY(CENTER)
                .scale(1.0f).pertN(0).pertAmp(0f)
                .build();
        StarParticle circular = new StarParticle(0f, 0f, 0f, 20f, 20f, 5000f, 1.0f, StarParticleType.STAR);

        rasterizer.rasterize(circular, grid);

        assertThat(grid.get(CENTER + 20, CENTER)).isGreaterThan(0.0f);
        assertThat(grid.get(CENTER - 20, CENTER)).isGreaterThan(0.0f);
        assertThat(grid.get(CENTER, CENTER + 20)).isGreaterThan(0.0f);
        assertThat(grid.get(CENTER, CENTER - 20)).isGreaterThan(0.0f);
    }

    @Test
    void rasterize_circular_orbit_does_not_mark_center() {
        DensityGrid grid = new DensityGrid(SIZE, SIZE);
        OrbitRasterizer rasterizer = OrbitRasterizer.builder()
                .centerX(CENTER).centerY(CENTER)
                .scale(1.0f).pertN(0).pertAmp(0f)
                .build();
        StarParticle circular = new StarParticle(0f, 0f, 0f, 20f, 20f, 5000f, 1.0f, StarParticleType.STAR);

        rasterizer.rasterize(circular, grid);

        assertThat(grid.get(CENTER, CENTER)).isZero();
    }

    @Test
    void rasterize_outside_grid_does_not_throw() {
        DensityGrid grid = new DensityGrid(SIZE, SIZE);
        OrbitRasterizer rasterizer = OrbitRasterizer.builder()
                .centerX(CENTER).centerY(CENTER)
                .scale(1.0f).pertN(0).pertAmp(0f)
                .build();
        StarParticle huge = new StarParticle(0f, 0f, 0f, 500f, 500f, 5000f, 1.0f, StarParticleType.STAR);

        rasterizer.rasterize(huge, grid);

        assertThat(grid.max()).isZero();
    }

    @Test
    void scale_factor_changes_orbit_size() {
        DensityGrid small = new DensityGrid(SIZE, SIZE);
        DensityGrid big = new DensityGrid(SIZE, SIZE);
        StarParticle p = new StarParticle(0f, 0f, 0f, 10f, 10f, 5000f, 1.0f, StarParticleType.STAR);

        OrbitRasterizer.builder().centerX(CENTER).centerY(CENTER).scale(1.0f).build().rasterize(p, small);
        OrbitRasterizer.builder().centerX(CENTER).centerY(CENTER).scale(2.0f).build().rasterize(p, big);

        assertThat(small.get(CENTER + 10, CENTER)).isGreaterThan(0.0f);
        assertThat(big.get(CENTER + 20, CENTER)).isGreaterThan(0.0f);
        assertThat(big.get(CENTER + 10, CENTER)).isZero();
    }
}
