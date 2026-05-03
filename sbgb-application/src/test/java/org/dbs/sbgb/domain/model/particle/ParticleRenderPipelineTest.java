package org.dbs.sbgb.domain.model.particle;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class ParticleRenderPipelineTest {

    @Test
    void renders_each_particle_through_the_rasterizer() {
        ParticleRasterizer<Integer> rasterizer = (p, grid) -> grid.add(p, p, 1.0f);
        ParticleRenderPipeline<Integer> pipeline = new ParticleRenderPipeline<>(
                rasterizer,
                RasterPostProcessor.builder().blurPasses(0).gamma(1.0f).build());

        DensityGrid grid = pipeline.render(List.of(1, 2, 3), 10, 10);

        assertThat(grid.get(1, 1)).isGreaterThan(0.0f);
        assertThat(grid.get(2, 2)).isGreaterThan(0.0f);
        assertThat(grid.get(3, 3)).isGreaterThan(0.0f);
    }

    @Test
    void applies_post_processor_after_rasterization() {
        ParticleRasterizer<Integer> rasterizer = (p, grid) -> {
            grid.add(0, 0, 2.0f);
            grid.add(1, 1, 4.0f);
        };
        ParticleRenderPipeline<Integer> pipeline = new ParticleRenderPipeline<>(
                rasterizer,
                RasterPostProcessor.builder().blurPasses(0).gamma(1.0f).build());

        DensityGrid grid = pipeline.render(List.of(0), 4, 4);

        assertThat(grid.max()).isEqualTo(1.0f, within(1e-6f));
        assertThat(grid.get(0, 0)).isEqualTo(0.5f, within(1e-6f));
    }

    @Test
    void returns_empty_grid_for_empty_particle_list() {
        ParticleRasterizer<Integer> rasterizer = (p, grid) -> grid.add(p, p, 1.0f);
        ParticleRenderPipeline<Integer> pipeline = new ParticleRenderPipeline<>(
                rasterizer,
                RasterPostProcessor.builder().blurPasses(0).gamma(1.0f).build());

        DensityGrid grid = pipeline.render(List.of(), 5, 5);

        assertThat(grid.max()).isZero();
    }
}
