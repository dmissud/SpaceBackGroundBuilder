package org.dbs.sbgb.domain.model.particle;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class RasterPostProcessorTest {

    @Test
    void normalize_scales_max_to_one() {
        DensityGrid grid = new DensityGrid(4, 4);
        grid.add(0, 0, 2.0f);
        grid.add(1, 1, 4.0f);
        grid.add(2, 2, 1.0f);

        RasterPostProcessor.builder().blurPasses(0).gamma(1.0f).build().apply(grid);

        assertThat(grid.max()).isEqualTo(1.0f, within(1e-6f));
        assertThat(grid.get(1, 1)).isEqualTo(1.0f, within(1e-6f));
        assertThat(grid.get(0, 0)).isEqualTo(0.5f, within(1e-6f));
    }

    @Test
    void normalize_on_empty_grid_does_nothing() {
        DensityGrid grid = new DensityGrid(4, 4);

        RasterPostProcessor.builder().blurPasses(0).gamma(1.0f).build().apply(grid);

        assertThat(grid.max()).isZero();
    }

    @Test
    void gamma_compresses_values_below_one() {
        DensityGrid grid = new DensityGrid(2, 2);
        grid.add(0, 0, 0.25f);

        RasterPostProcessor.builder().blurPasses(0).gamma(0.5f).build().apply(grid);

        // Normalize takes 0.25 → 1.0, then gamma 0.5 → 1.0^0.5 = 1.0 (stays max).
        // Add a second value to exercise gamma curve.
        DensityGrid grid2 = new DensityGrid(2, 2);
        grid2.add(0, 0, 1.0f);
        grid2.add(1, 1, 0.25f);
        RasterPostProcessor.builder().blurPasses(0).gamma(0.5f).build().apply(grid2);
        assertThat(grid2.get(1, 1)).isEqualTo((float) Math.pow(0.25, 0.5), within(1e-6f));
    }

    @Test
    void blur_spreads_intensity_to_neighbors() {
        DensityGrid grid = new DensityGrid(11, 11);
        grid.add(5, 5, 1.0f);

        RasterPostProcessor.builder().blurPasses(1).blurRadius(2).gamma(1.0f).build().apply(grid);

        assertThat(grid.get(5, 5)).isEqualTo(grid.max());
        assertThat(grid.get(4, 5)).isGreaterThan(0.0f);
        assertThat(grid.get(5, 4)).isGreaterThan(0.0f);
        assertThat(grid.get(6, 6)).isGreaterThan(0.0f);
    }

    @Test
    void zero_blur_passes_keeps_grid_unchanged_before_normalize() {
        DensityGrid grid = new DensityGrid(5, 5);
        grid.add(2, 2, 1.0f);

        RasterPostProcessor.builder().blurPasses(0).gamma(1.0f).build().apply(grid);

        assertThat(grid.get(2, 2)).isEqualTo(1.0f, within(1e-6f));
        assertThat(grid.get(1, 2)).isZero();
    }
}
