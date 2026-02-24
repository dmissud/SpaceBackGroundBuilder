package org.dbs.sbgb.domain.strategy;

import org.dbs.sbgb.domain.model.GalaxyIntensityCalculator;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
import org.dbs.sbgb.domain.model.PerlinGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class GalaxyGeneratorFactoryTest {

    private GalaxyGeneratorFactory factory;
    private PerlinGenerator noiseGenerator;

    @BeforeEach
    void setUp() {
        List<GalaxyGeneratorStrategy> strategies = List.of(
                new SpiralGeneratorStrategy(),
                new VoronoiGeneratorStrategy(),
                new EllipticalGeneratorStrategy(),
                new RingGeneratorStrategy(),
                new IrregularGeneratorStrategy()
        );
        factory = new GalaxyGeneratorFactory(strategies);
        noiseGenerator = mock(PerlinGenerator.class);
    }

    @Test
    void shouldCreateSpiralGenerator() {
        // Given
        GalaxyParameters params = GalaxyParameters.createDefault();
        GalaxyGenerationContext context = createContext(params);

        // When
        GalaxyIntensityCalculator calculator = factory.create(GalaxyType.SPIRAL, context);

        // Then
        assertThat(calculator).isNotNull();
    }

    @Test
    void shouldCreateVoronoiGenerator() {
        // Given
        GalaxyParameters params = GalaxyParameters.createDefaultVoronoi();
        GalaxyGenerationContext context = createContext(params);

        // When
        GalaxyIntensityCalculator calculator = factory.create(GalaxyType.VORONOI_CLUSTER, context);

        // Then
        assertThat(calculator).isNotNull();
    }

    @Test
    void shouldCreateEllipticalGenerator() {
        // Given
        GalaxyParameters params = GalaxyParameters.createDefaultElliptical();
        GalaxyGenerationContext context = createContext(params);

        // When
        GalaxyIntensityCalculator calculator = factory.create(GalaxyType.ELLIPTICAL, context);

        // Then
        assertThat(calculator).isNotNull();
    }

    @Test
    void shouldCreateRingGenerator() {
        // Given
        GalaxyParameters params = GalaxyParameters.createDefaultRing();
        GalaxyGenerationContext context = createContext(params);

        // When
        GalaxyIntensityCalculator calculator = factory.create(GalaxyType.RING, context);

        // Then
        assertThat(calculator).isNotNull();
    }

    @Test
    void shouldCreateIrregularGenerator() {
        // Given
        GalaxyParameters params = GalaxyParameters.createDefaultIrregular();
        GalaxyGenerationContext context = createContext(params);

        // When
        GalaxyIntensityCalculator calculator = factory.create(GalaxyType.IRREGULAR, context);

        // Then
        assertThat(calculator).isNotNull();
    }

    @Test
    void shouldThrowExceptionForUnsupportedType() {
        // Given
        GalaxyParameters params = GalaxyParameters.createDefault();
        GalaxyGenerationContext context = createContext(params);

        // When/Then
        assertThatThrownBy(() -> factory.create(null, context))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported galaxy type");
    }

    private GalaxyGenerationContext createContext(GalaxyParameters params) {
        return GalaxyGenerationContext.builder()
                .width(1000)
                .height(1000)
                .noiseGenerator(noiseGenerator)
                .seed(12345L)
                .parameters(params)
                .build();
    }
}
