package org.dbs.sbgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IrregularGalaxyGeneratorTest {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    private static final long SEED = 42L;
    private static final double GALAXY_RADIUS = 200.0;
    private static final double CORE_SIZE = 0.05;
    private static final double IRREGULARITY = 0.8;
    private static final int CLUMP_COUNT = 15;
    private static final double CLUMP_SIZE = 40.0;

    private IrregularGalaxyGenerator generator;

    @BeforeEach
    void setUp() {
        PerlinGenerator noiseGenerator = new PerlinGenerator(Interpolation.LINEAR, FadeFunction.NONE);
        noiseGenerator.createNoisePipeline(SEED, WIDTH, HEIGHT, 4, 0.5, 2.0, 200.0, NoiseType.FBM);
        noiseGenerator.performNormalization();

        generator = IrregularGalaxyGenerator.builder()
                .width(WIDTH)
                .height(HEIGHT)
                .noiseGenerator(noiseGenerator)
                .seed(SEED)
                .coreSize(CORE_SIZE)
                .galaxyRadius(GALAXY_RADIUS)
                .irregularity(IRREGULARITY)
                .clumpCount(CLUMP_COUNT)
                .clumpSize(CLUMP_SIZE)
                .build();
    }

    @Test
    void shouldReturnZeroOutsideGalaxyRadius() {
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;
        // Point well outside galaxy radius
        int farX = centerX + (int) (GALAXY_RADIUS * 1.5);
        int farY = centerY;

        double intensity = generator.calculateGalaxyIntensity(farX, farY);

        assertThat(intensity).isEqualTo(0.0);
    }

    @Test
    void shouldReturnHighIntensityNearCore() {
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;

        double intensity = generator.calculateGalaxyIntensity(centerX, centerY);

        assertThat(intensity).isGreaterThan(0.1);
    }

    @Test
    void shouldHaveIrregularDistribution() {
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;
        double testRadius = GALAXY_RADIUS * 0.5;

        // Sample multiple points at same distance - should have high variance for irregular galaxy
        double intensity1 = generator.calculateGalaxyIntensity(centerX + (int) testRadius, centerY);
        double intensity2 = generator.calculateGalaxyIntensity(centerX, centerY + (int) testRadius);
        double intensity3 = generator.calculateGalaxyIntensity(centerX - (int) testRadius, centerY);
        double intensity4 = generator.calculateGalaxyIntensity(centerX, centerY - (int) testRadius);

        // For irregular galaxy, intensities at same radius should vary significantly
        double maxIntensity = Math.max(Math.max(intensity1, intensity2), Math.max(intensity3, intensity4));
        double minIntensity = Math.min(Math.min(intensity1, intensity2), Math.min(intensity3, intensity4));

        // High irregularity means variance > 0.03 (irregular galaxies have asymmetry)
        assertThat(maxIntensity - minIntensity).isGreaterThan(0.03);
    }

    @Test
    void shouldBeReproducibleWithSameSeed() {
        PerlinGenerator noiseGenerator2 = new PerlinGenerator(Interpolation.LINEAR, FadeFunction.NONE);
        noiseGenerator2.createNoisePipeline(SEED, WIDTH, HEIGHT, 4, 0.5, 2.0, 200.0, NoiseType.FBM);
        noiseGenerator2.performNormalization();

        IrregularGalaxyGenerator generator2 = IrregularGalaxyGenerator.builder()
                .width(WIDTH)
                .height(HEIGHT)
                .noiseGenerator(noiseGenerator2)
                .seed(SEED)
                .coreSize(CORE_SIZE)
                .galaxyRadius(GALAXY_RADIUS)
                .irregularity(IRREGULARITY)
                .clumpCount(CLUMP_COUNT)
                .clumpSize(CLUMP_SIZE)
                .build();

        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;

        assertThat(generator.calculateGalaxyIntensity(centerX, centerY))
                .isEqualTo(generator2.calculateGalaxyIntensity(centerX, centerY));
        assertThat(generator.calculateGalaxyIntensity(centerX + 50, centerY + 30))
                .isEqualTo(generator2.calculateGalaxyIntensity(centerX + 50, centerY + 30));
        assertThat(generator.calculateGalaxyIntensity(centerX - 80, centerY - 60))
                .isEqualTo(generator2.calculateGalaxyIntensity(centerX - 80, centerY - 60));
    }

    @Test
    void shouldReturnValuesBetweenZeroAndOne() {
        for (int x = 0; x < WIDTH; x += 10) {
            for (int y = 0; y < HEIGHT; y += 10) {
                double intensity = generator.calculateGalaxyIntensity(x, y);
                assertThat(intensity).isBetween(0.0, 1.0);
            }
        }
    }
}
