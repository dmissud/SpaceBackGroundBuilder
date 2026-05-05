package org.dbs.sbgb.domain.model.densitywave;

import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.particle.DensityGrid;
import org.dbs.sbgb.domain.model.particle.OrbitRasterizer;
import org.dbs.sbgb.domain.model.particle.ParticleRenderPipeline;
import org.dbs.sbgb.domain.model.particle.RasterPostProcessor;

import java.util.List;

@Slf4j
public class DensityWaveIntensityCalculator implements GalaxyIntensityCalculator {

    private static final float ZOOM_FACTOR = 0.5f;
    private static final float GAMMA = 0.45f;
    private static final int GLOW_BLUR_PASSES = 3;

    private final DensityGrid grid;

    public DensityWaveIntensityCalculator(int width, int height, long seed, DensityWaveGalaxyParams params) {
        log.info("[DensityWave] Generating {} particles (seed={}, {}x{})", params.starCount(), seed, width, height);
        List<StarParticle> particles = new DensityWaveGalaxyGenerator(params, seed).generate();
        log.info("[DensityWave] {} particles generated — running render pipeline...", particles.size());

        ParticleRenderPipeline<StarParticle> pipeline = new ParticleRenderPipeline<>(
                OrbitRasterizer.builder()
                        .centerX(width / 2)
                        .centerY(height / 2)
                        .scale(computeScale(width, height, params.galaxyRadius()))
                        .pertN(params.pertN())
                        .pertAmp(params.pertAmp())
                        .build(),
                RasterPostProcessor.builder()
                        .blurPasses(GLOW_BLUR_PASSES)
                        .blurRadius(computeBlurRadius(width))
                        .gamma(GAMMA)
                        .build());

        this.grid = pipeline.render(particles, width, height);
        log.info("[DensityWave] Done.");
    }

    @Override
    public double calculateGalaxyIntensity(int x, int y) {
        return grid.get(x, y);
    }

    private float computeScale(int width, int height, float galaxyRadius) {
        return Math.min(width, height) / (2.0f * galaxyRadius * ZOOM_FACTOR);
    }

    private int computeBlurRadius(int width) {
        return Math.max(3, width / 500);
    }
}
