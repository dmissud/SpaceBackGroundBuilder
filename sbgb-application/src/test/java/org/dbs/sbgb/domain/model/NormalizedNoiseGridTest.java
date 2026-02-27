package org.dbs.sbgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;

class NormalizedNoiseGridTest {

    private static final int WIDTH = 100;
    private static final int HEIGHT = 100;

    @Test
    void shouldReturnNormalizedValuesBetweenZeroAndOne() {
        NormalizedNoiseGrid grid = computeGrid();

        for (int x = 0; x < WIDTH; x += 10) {
            for (int y = 0; y < HEIGHT; y += 10) {
                assertThat(grid.normalizedValueAt(x, y)).isBetween(0.0, 1.0);
            }
        }
    }

    @Test
    void shouldProduceImageWithCorrectDimensions() {
        NormalizedNoiseGrid grid = computeGrid();
        DefaultNoiseColorCalculator colorCalculator = new DefaultNoiseColorCalculator(
                java.awt.Color.BLACK, java.awt.Color.ORANGE, java.awt.Color.WHITE,
                0.4, 0.7, InterpolationType.LINEAR, false);

        BufferedImage image = grid.renderWithColors(colorCalculator);

        assertThat(image.getWidth()).isEqualTo(WIDTH);
        assertThat(image.getHeight()).isEqualTo(HEIGHT);
    }

    @Test
    void shouldProduceConsistentResultsForSameSeed() {
        NormalizedNoiseGrid grid1 = computeGrid();
        NormalizedNoiseGrid grid2 = computeGrid();

        assertThat(grid1.normalizedValueAt(50, 50)).isEqualTo(grid2.normalizedValueAt(50, 50));
    }

    private NormalizedNoiseGrid computeGrid() {
        PerlinGenerator generator = new PerlinGenerator(Interpolation.LINEAR, FadeFunction.NONE);
        return generator.computeAndNormalize(42L, WIDTH, HEIGHT, 3, 0.5, 2.0, 100.0, NoiseType.FBM);
    }
}
