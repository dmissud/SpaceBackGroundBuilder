package org.dbs.sbgb.domain.model.particle;

public interface ParticleRasterizer<P> {

    void rasterize(P particle, DensityGrid grid);
}
