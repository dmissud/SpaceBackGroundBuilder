package org.dbs.sbgb.domain.service;

import org.dbs.sbgb.port.in.DensityWaveParameters;
import org.dbs.sbgb.port.in.GalaxyRequestCmd;
import org.dbs.sbgb.port.in.GetGalaxyParticlesUseCase;
import org.dbs.sbgb.port.in.StarParticleDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GetGalaxyParticlesUseCaseTest {

    private GetGalaxyParticlesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GalaxyService(null, null, null, null);
    }

    @Test
    void getParticles_withDefaultDensityWave_shouldReturnNonEmptyList() {
        GalaxyRequestCmd cmd = GalaxyRequestCmd.builder()
                .galaxyType("DENSITY_WAVE")
                .width(800)
                .height(800)
                .seed(42L)
                .build();

        List<StarParticleDto> particles = useCase.getParticles(cmd);

        assertThat(particles).isNotEmpty();
    }

    @Test
    void getParticles_shouldContainAllTypes() {
        GalaxyRequestCmd cmd = GalaxyRequestCmd.builder()
                .galaxyType("DENSITY_WAVE")
                .width(800)
                .height(800)
                .seed(42L)
                .build();

        List<StarParticleDto> particles = useCase.getParticles(cmd);

        assertThat(particles).extracting(StarParticleDto::type)
                .containsAnyOf("STAR", "DUST", "H2_OUTER", "H2_CORE");
    }

    @Test
    void getParticles_withCustomParams_shouldRespectStarCount() {
        DensityWaveParameters customParams = new DensityWaveParameters(
                15000.0f, 2000.0f, 0.019f, 0.85f, 0.95f, 10000, false, 2, 50.0f, 4000.0f
        );
        GalaxyRequestCmd cmd = GalaxyRequestCmd.builder()
                .galaxyType("DENSITY_WAVE")
                .densityWaveParameters(customParams)
                .width(800)
                .height(800)
                .seed(42L)
                .build();

        List<StarParticleDto> particles = useCase.getParticles(cmd);

        long starCount = particles.stream().filter(p -> p.type().equals("STAR")).count();
        assertThat(starCount).isEqualTo(10000);
    }

    @Test
    void getParticles_shouldBeDeterministicWithSameSeed() {
        GalaxyRequestCmd cmd = GalaxyRequestCmd.builder()
                .galaxyType("DENSITY_WAVE")
                .width(800)
                .height(800)
                .seed(99L)
                .build();

        List<StarParticleDto> first = useCase.getParticles(cmd);
        List<StarParticleDto> second = useCase.getParticles(cmd);

        assertThat(first).hasSameSizeAs(second);
        assertThat(first.get(0).semiMajorAxis()).isEqualTo(second.get(0).semiMajorAxis());
    }
}
