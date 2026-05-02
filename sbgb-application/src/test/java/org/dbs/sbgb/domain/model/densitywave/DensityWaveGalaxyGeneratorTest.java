package org.dbs.sbgb.domain.model.densitywave;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DensityWaveGalaxyGeneratorTest {

    private static final int STAR_COUNT = 5000;
    private static final float GALAXY_RADIUS = 15000.0f;
    private static final long SEED = 42L;

    private DensityWaveGalaxyGenerator generator;
    private List<StarParticle> particles;

    @BeforeEach
    void setUp() {
        DensityWaveGalaxyParams params = DensityWaveGalaxyParams.defaultParams(GALAXY_RADIUS, STAR_COUNT);
        generator = new DensityWaveGalaxyGenerator(params, SEED);
        particles = generator.generate();
    }

    @Test
    void generates_stars_dust_and_h2_particles() {
        assertThat(particles).isNotEmpty();
    }

    @Test
    void total_particle_count_is_greater_than_star_count() {
        assertThat(particles.size()).isGreaterThan(STAR_COUNT);
    }

    @Test
    void contains_all_particle_types() {
        assertThat(particles).extracting(StarParticle::type)
                .contains(StarParticleType.STAR, StarParticleType.DUST,
                          StarParticleType.H2_OUTER, StarParticleType.H2_CORE);
    }

    @Test
    void star_count_matches_requested() {
        long starCount = particles.stream()
                .filter(p -> p.type() == StarParticleType.STAR)
                .count();
        assertThat(starCount).isEqualTo(STAR_COUNT);
    }

    @Test
    void h2_regions_come_in_pairs() {
        long outerCount = particles.stream().filter(p -> p.type() == StarParticleType.H2_OUTER).count();
        long coreCount = particles.stream().filter(p -> p.type() == StarParticleType.H2_CORE).count();
        assertThat(outerCount).isEqualTo(coreCount);
    }

    @Test
    void h2_density_1_produces_100_regions() {
        DensityWaveGalaxyParams params = new DensityWaveGalaxyParams(
                GALAXY_RADIUS, 2250f, 0.019f, 0.85f, 0.95f, STAR_COUNT, false, 0, 0f, 4000f, 1);
        List<StarParticle> result = new DensityWaveGalaxyGenerator(params, SEED).generate();
        long outerCount = result.stream().filter(p -> p.type() == StarParticleType.H2_OUTER).count();
        assertThat(outerCount).isEqualTo(100);
    }

    @Test
    void h2_density_5_produces_1200_regions() {
        DensityWaveGalaxyParams params = new DensityWaveGalaxyParams(
                GALAXY_RADIUS, 2250f, 0.019f, 0.85f, 0.95f, STAR_COUNT, false, 0, 0f, 4000f, 5);
        List<StarParticle> result = new DensityWaveGalaxyGenerator(params, SEED).generate();
        long outerCount = result.stream().filter(p -> p.type() == StarParticleType.H2_OUTER).count();
        assertThat(outerCount).isEqualTo(1200);
    }

    @Test
    void all_particles_have_positive_semi_major_axis() {
        assertThat(particles).allSatisfy(p -> assertThat(p.semiMajorAxis()).isGreaterThanOrEqualTo(0));
    }

    @Test
    void all_particles_have_positive_magnitude() {
        assertThat(particles).allSatisfy(p -> assertThat(p.magnitude()).isGreaterThan(0));
    }

    @Test
    void all_particles_have_positive_temperature() {
        assertThat(particles).allSatisfy(p -> assertThat(p.temperature()).isGreaterThan(0));
    }

    @Test
    void generation_is_reproducible_with_same_seed() {
        DensityWaveGalaxyParams params = DensityWaveGalaxyParams.defaultParams(GALAXY_RADIUS, STAR_COUNT);
        List<StarParticle> second = new DensityWaveGalaxyGenerator(params, SEED).generate();
        assertThat(particles).hasSameSizeAs(second);
        assertThat(particles.get(0).semiMajorAxis()).isEqualTo(second.get(0).semiMajorAxis());
    }
}
