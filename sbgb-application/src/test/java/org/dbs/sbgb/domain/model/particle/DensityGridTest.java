package org.dbs.sbgb.domain.model.particle;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class DensityGridTest {

    @Test
    void new_grid_returns_zero_everywhere() {
        DensityGrid grid = new DensityGrid(10, 10);
        assertThat(grid.get(5, 5)).isZero();
    }

    @Test
    void add_accumulates_intensity() {
        DensityGrid grid = new DensityGrid(10, 10);
        grid.add(3, 4, 0.5f);
        grid.add(3, 4, 0.25f);
        assertThat(grid.get(3, 4)).isEqualTo(0.75f, within(1e-6f));
    }

    @Test
    void add_outside_bounds_is_ignored() {
        DensityGrid grid = new DensityGrid(10, 10);
        grid.add(-1, 0, 1.0f);
        grid.add(0, 10, 1.0f);
        grid.add(10, 0, 1.0f);
        assertThat(grid.get(0, 0)).isZero();
    }

    @Test
    void get_outside_bounds_returns_zero() {
        DensityGrid grid = new DensityGrid(10, 10);
        assertThat(grid.get(-1, 0)).isZero();
        assertThat(grid.get(10, 0)).isZero();
        assertThat(grid.get(0, -1)).isZero();
        assertThat(grid.get(0, 10)).isZero();
    }

    @Test
    void max_returns_highest_value() {
        DensityGrid grid = new DensityGrid(5, 5);
        grid.add(0, 0, 0.3f);
        grid.add(2, 3, 0.9f);
        grid.add(4, 4, 0.5f);
        assertThat(grid.max()).isEqualTo(0.9f, within(1e-6f));
    }

    @Test
    void max_on_empty_grid_is_zero() {
        DensityGrid grid = new DensityGrid(5, 5);
        assertThat(grid.max()).isZero();
    }

    @Test
    void exposes_dimensions() {
        DensityGrid grid = new DensityGrid(7, 11);
        assertThat(grid.width()).isEqualTo(7);
        assertThat(grid.height()).isEqualTo(11);
    }
}
