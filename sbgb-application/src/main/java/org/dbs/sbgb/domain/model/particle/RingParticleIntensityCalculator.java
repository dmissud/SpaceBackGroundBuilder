package org.dbs.sbgb.domain.model.particle;

import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.parameters.CoreParameters;
import org.dbs.sbgb.domain.model.parameters.RingStructureParameters;

import java.util.List;

@Slf4j
public class RingParticleIntensityCalculator implements GalaxyIntensityCalculator {

    private static final float ZOOM_FACTOR = 0.4f;
    private static final float GAMMA = 0.45f;
    private static final int BLUR_PASSES = 3;
    private static final float PARTICLES_PER_PIXEL = 1.0f / 30.0f;

    private final DensityGrid grid;

    public RingParticleIntensityCalculator(int width, int height,
                                           CoreParameters core,
                                           RingStructureParameters ring,
                                           long seed) {
        int totalCount = Math.max(2000, (int) (width * height * PARTICLES_PER_PIXEL));
        log.info("[Ring] Generating {} particles (seed={}, {}x{})", totalCount, seed, width, height);

        List<StellarPoint> particles = new RingParticleGenerator(
                (float) core.getGalaxyRadius(), ring, totalCount, seed).generate();
        log.info("[Ring] {} particles — rendering pipeline...", particles.size());

        float scale = Math.min(width, height) / (2.0f * (float) core.getGalaxyRadius() * ZOOM_FACTOR);

        this.grid = new ParticleRenderPipeline<>(
                PointRasterizer.builder()
                        .centerX(width / 2)
                        .centerY(height / 2)
                        .scale(scale)
                        .splatRadius(2)
                        .build(),
                RasterPostProcessor.builder()
                        .blurPasses(BLUR_PASSES)
                        .blurRadius(Math.max(4, width / 350))
                        .gamma(GAMMA)
                        .build()
        ).render(particles, width, height);

        log.info("[Ring] Done.");
    }

    @Override
    public double calculateGalaxyIntensity(int x, int y) {
        return grid.get(x, y);
    }
}
