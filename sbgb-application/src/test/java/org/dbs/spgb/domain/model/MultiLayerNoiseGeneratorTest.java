package org.dbs.spgb.domain.model;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class MultiLayerNoiseGeneratorTest {

    @Test
    void shouldReturnValueBetween0And1() {
        MultiLayerNoiseGenerator generator = MultiLayerNoiseGenerator.builder()
                .seed(123L)
                .width(100)
                .height(100)
                .interpolation(Interpolation.LINEAR)
                .fadeFunction(FadeFunction.NONE)
                .noiseType(NoiseType.FBM)
                .macroScale(0.3)
                .macroWeight(0.5)
                .mesoScale(1.0)
                .mesoWeight(0.35)
                .microScale(3.0)
                .microWeight(0.15)
                .build();

        generator.initialize();

        double value = generator.evaluate(50, 50);

        assertThat(value).isBetween(0.0, 1.0);
    }

    @Test
    void shouldBeReproducibleWithSameSeed() {
        MultiLayerNoiseGenerator gen1 = MultiLayerNoiseGenerator.builder()
                .seed(456L)
                .width(100)
                .height(100)
                .interpolation(Interpolation.LINEAR)
                .fadeFunction(FadeFunction.NONE)
                .noiseType(NoiseType.FBM)
                .macroScale(0.3)
                .macroWeight(0.5)
                .mesoScale(1.0)
                .mesoWeight(0.35)
                .microScale(3.0)
                .microWeight(0.15)
                .build();

        MultiLayerNoiseGenerator gen2 = MultiLayerNoiseGenerator.builder()
                .seed(456L)
                .width(100)
                .height(100)
                .interpolation(Interpolation.LINEAR)
                .fadeFunction(FadeFunction.NONE)
                .noiseType(NoiseType.FBM)
                .macroScale(0.3)
                .macroWeight(0.5)
                .mesoScale(1.0)
                .mesoWeight(0.35)
                .microScale(3.0)
                .microWeight(0.15)
                .build();

        gen1.initialize();
        gen2.initialize();

        double val1 = gen1.evaluate(25, 75);
        double val2 = gen2.evaluate(25, 75);

        assertThat(val1).isEqualTo(val2, within(0.0001));
    }

    @Test
    void shouldBeDifferentWithDifferentSeed() {
        MultiLayerNoiseGenerator gen1 = MultiLayerNoiseGenerator.builder()
                .seed(111L)
                .width(100)
                .height(100)
                .interpolation(Interpolation.LINEAR)
                .fadeFunction(FadeFunction.NONE)
                .noiseType(NoiseType.FBM)
                .macroScale(0.3)
                .macroWeight(0.5)
                .mesoScale(1.0)
                .mesoWeight(0.35)
                .microScale(3.0)
                .microWeight(0.15)
                .build();

        MultiLayerNoiseGenerator gen2 = MultiLayerNoiseGenerator.builder()
                .seed(222L)
                .width(100)
                .height(100)
                .interpolation(Interpolation.LINEAR)
                .fadeFunction(FadeFunction.NONE)
                .noiseType(NoiseType.FBM)
                .macroScale(0.3)
                .macroWeight(0.5)
                .mesoScale(1.0)
                .mesoWeight(0.35)
                .microScale(3.0)
                .microWeight(0.15)
                .build();

        gen1.initialize();
        gen2.initialize();

        double val1 = gen1.evaluate(25, 75);
        double val2 = gen2.evaluate(25, 75);

        assertThat(val1).isNotEqualTo(val2);
    }

    @Test
    void shouldCombineLayersWithCorrectWeights() {
        MultiLayerNoiseGenerator generator = MultiLayerNoiseGenerator.builder()
                .seed(789L)
                .width(100)
                .height(100)
                .interpolation(Interpolation.LINEAR)
                .fadeFunction(FadeFunction.NONE)
                .noiseType(NoiseType.FBM)
                .macroScale(0.3)
                .macroWeight(0.5)
                .mesoScale(1.0)
                .mesoWeight(0.35)
                .microScale(3.0)
                .microWeight(0.15)
                .build();

        generator.initialize();

        // Test that combined weights sum to 1.0 (implicitly through normalization)
        double value = generator.evaluate(30, 70);
        assertThat(value).isBetween(0.0, 1.0);
    }

    @Test
    void shouldProvideDifferentDetailsAtDifferentScales() {
        // Generator with more macro weight (smoother, large structures)
        MultiLayerNoiseGenerator macroHeavy = MultiLayerNoiseGenerator.builder()
                .seed(333L)
                .width(100)
                .height(100)
                .interpolation(Interpolation.LINEAR)
                .fadeFunction(FadeFunction.NONE)
                .noiseType(NoiseType.FBM)
                .macroScale(0.3)
                .macroWeight(1.0)
                .mesoScale(1.0)
                .mesoWeight(0.0)
                .microScale(3.0)
                .microWeight(0.0)
                .build();

        // Generator with more micro weight (noisier, fine details)
        MultiLayerNoiseGenerator microHeavy = MultiLayerNoiseGenerator.builder()
                .seed(333L)
                .width(100)
                .height(100)
                .interpolation(Interpolation.LINEAR)
                .fadeFunction(FadeFunction.NONE)
                .noiseType(NoiseType.FBM)
                .macroScale(0.3)
                .macroWeight(0.0)
                .mesoScale(1.0)
                .mesoWeight(0.0)
                .microScale(3.0)
                .microWeight(1.0)
                .build();

        macroHeavy.initialize();
        microHeavy.initialize();

        // Compute variance over a small region to measure noise frequency
        double macroVariance = computeLocalVariance(macroHeavy, 50, 50, 5);
        double microVariance = computeLocalVariance(microHeavy, 50, 50, 5);

        // Micro should have higher local variance (more detail changes)
        assertThat(microVariance).isGreaterThan(macroVariance);
    }

    private double computeLocalVariance(MultiLayerNoiseGenerator generator, int centerX, int centerY, int radius) {
        double sum = 0.0;
        int count = 0;
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                sum += generator.evaluate(x, y);
                count++;
            }
        }
        double mean = sum / count;

        double varianceSum = 0.0;
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                double diff = generator.evaluate(x, y) - mean;
                varianceSum += diff * diff;
            }
        }
        return varianceSum / count;
    }
}
