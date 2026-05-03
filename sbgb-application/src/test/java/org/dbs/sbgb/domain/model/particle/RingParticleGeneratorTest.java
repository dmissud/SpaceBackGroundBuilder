package org.dbs.sbgb.domain.model.particle;

import org.dbs.sbgb.domain.model.parameters.RingStructureParameters;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RingParticleGeneratorTest {

    private static final float GALAXY_RADIUS = 1500f;
    private static final long SEED = 42L;

    private RingParticleGenerator generator(RingStructureParameters params) {
        return new RingParticleGenerator(GALAXY_RADIUS, params, 5000, SEED);
    }

    @Test
    void generates_non_empty_particle_list() {
        List<StellarPoint> points = generator(defaultParams()).generate();
        assertThat(points).isNotEmpty();
    }

    @Test
    void ring_stars_concentrated_near_ring_radius() {
        RingStructureParameters params = RingStructureParameters.builder()
                .ringRadius(900).ringWidth(50).ringIntensity(1.0).coreToRingRatio(0.0).build();
        List<StellarPoint> points = generator(params).generate();

        long nearRing = points.stream()
                .filter(p -> {
                    double r = Math.sqrt(p.x() * p.x() + p.y() * p.y());
                    return Math.abs(r - 900) < 200;
                })
                .count();

        assertThat(nearRing).isGreaterThan(points.size() / 2);
    }

    @Test
    void core_particles_concentrated_near_center_when_ratio_is_high() {
        RingStructureParameters params = RingStructureParameters.builder()
                .ringRadius(900).ringWidth(80).ringIntensity(0.1).coreToRingRatio(1.0).build();
        List<StellarPoint> points = generator(params).generate();

        long nearCenter = points.stream()
                .filter(p -> Math.sqrt(p.x() * p.x() + p.y() * p.y()) < 300)
                .count();

        assertThat(nearCenter).isGreaterThan(0);
    }

    @Test
    void all_magnitudes_are_positive() {
        List<StellarPoint> points = generator(defaultParams()).generate();
        points.forEach(p -> assertThat(p.magnitude()).isGreaterThan(0.0f));
    }

    @Test
    void h2_clusters_have_high_temperature() {
        List<StellarPoint> points = generator(defaultParams()).generate();
        float maxTemp = points.stream()
                .map(StellarPoint::temperature)
                .max(Float::compareTo)
                .orElseThrow();
        assertThat(maxTemp).isGreaterThan(8000f);
    }

    @Test
    void same_seed_produces_same_result() {
        List<StellarPoint> a = generator(defaultParams()).generate();
        List<StellarPoint> b = generator(defaultParams()).generate();
        assertThat(a).hasSize(b.size());
        assertThat(a.get(0).x()).isEqualTo(b.get(0).x());
    }

    private RingStructureParameters defaultParams() {
        return RingStructureParameters.builder().build();
    }
}
