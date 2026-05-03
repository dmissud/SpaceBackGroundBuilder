package org.dbs.sbgb.domain.model.particle;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ParticleRenderPipeline<P> {

    private final ParticleRasterizer<P> rasterizer;
    private final RasterPostProcessor postProcessor;

    public DensityGrid render(List<P> particles, int width, int height) {
        DensityGrid grid = new DensityGrid(width, height);
        for (P particle : particles) {
            rasterizer.rasterize(particle, grid);
        }
        postProcessor.apply(grid);
        return grid;
    }
}
