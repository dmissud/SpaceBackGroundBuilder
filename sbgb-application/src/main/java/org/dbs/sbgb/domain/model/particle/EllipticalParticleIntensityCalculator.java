package org.dbs.sbgb.domain.model.particle;

import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.parameters.EllipticalShapeParameters;

import java.util.List;

@Slf4j
public class EllipticalParticleIntensityCalculator implements GalaxyIntensityCalculator {

    private static final float ZOOM_FACTOR = 0.5f;
    private static final float GAMMA = 0.5f;
    private static final int BLUR_PASSES = 2;

    private final DensityGrid grid;

    public EllipticalParticleIntensityCalculator(int width, int height,
                                                 float galaxyRadius, int starCount,
                                                 EllipticalShapeParameters shape, long seed) {
        log.info("[Elliptical] Generating {} particles (seed={}, {}x{})", starCount, seed, width, height);
        List<StellarPoint> particles = new EllipticalParticleGenerator(galaxyRadius, starCount, shape, seed).generate();
        log.info("[Elliptical] {} particles generated — running render pipeline...", particles.size());

        float scale = Math.min(width, height) / (2.0f * galaxyRadius * ZOOM_FACTOR);

        this.grid = new ParticleRenderPipeline<>(
                PointRasterizer.builder()
                        .centerX(width / 2)
                        .centerY(height / 2)
                        .scale(scale)
                        .splatRadius(2)
                        .build(),
                RasterPostProcessor.builder()
                        .blurPasses(BLUR_PASSES)
                        .blurRadius(Math.max(3, width / 400))
                        .gamma(GAMMA)
                        .build()
        ).render(particles, width, height);

        log.info("[Elliptical] Done.");
    }

    @Override
    public double calculateGalaxyIntensity(int x, int y) {
        return grid.get(x, y);
    }
}
