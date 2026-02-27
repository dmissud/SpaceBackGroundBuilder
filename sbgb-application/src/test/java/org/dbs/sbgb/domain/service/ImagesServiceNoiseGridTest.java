package org.dbs.sbgb.domain.service;

import cucumber.steps.NoiseBaseStructureRepositoryStub;
import cucumber.steps.NoiseCosmeticRenderRepositoryStub;
import cucumber.steps.NoiseGridComputationPortStub;
import org.dbs.sbgb.domain.model.NormalizedNoiseGrid;
import org.dbs.sbgb.port.in.ImageRequestCmd;
import org.dbs.sbgb.port.out.NoiseGridComputationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ImagesServiceNoiseGridTest {

    private CountingNoiseGridPortStub noiseGridPort;
    private ImagesService imagesService;

    @BeforeEach
    void setUp() {
        noiseGridPort = new CountingNoiseGridPortStub();
        imagesService = new ImagesService(
                new NoiseBaseStructureRepositoryStub(),
                new NoiseCosmeticRenderRepositoryStub(),
                noiseGridPort);
    }

    @Test
    void shouldDelegateNoiseGridComputationToPort() throws IOException {
        ImageRequestCmd cmd = buildCmd();

        imagesService.buildNoiseImage(cmd);

        assertThat(noiseGridPort.computeCount()).isEqualTo(1);
    }

    @Test
    void shouldCallPortForEachBuildRequest() throws IOException {
        ImageRequestCmd cmd = buildCmd();

        imagesService.buildNoiseImage(cmd);
        imagesService.buildNoiseImage(cmd);

        assertThat(noiseGridPort.computeCount()).isEqualTo(2);
    }

    private ImageRequestCmd buildCmd() {
        return ImageRequestCmd.builder()
                .sizeCmd(ImageRequestCmd.SizeCmd.builder()
                        .width(100).height(100).seed(42)
                        .octaves(1).persistence(0.5).lacunarity(2.0).scale(100.0)
                        .noiseType("FBM").useMultiLayer(false)
                        .build())
                .colorCmd(ImageRequestCmd.ColorCmd.builder()
                        .back("#000000").backThreshold(0.4)
                        .middle("#FFA500").middleThreshold(0.7)
                        .fore("#FFFFFF").interpolationType("LINEAR")
                        .transparentBackground(false)
                        .build())
                .build();
    }

    /** Stub comptant les appels pour vérifier la délégation. */
    private static class CountingNoiseGridPortStub implements NoiseGridComputationPort {

        private final AtomicInteger count = new AtomicInteger(0);
        private final NoiseGridComputationPortStub delegate = new NoiseGridComputationPortStub();

        @Override
        public NormalizedNoiseGrid computeSingleLayerGrid(int configHash, ImageRequestCmd.SizeCmd sizeCmd) {
            count.incrementAndGet();
            return delegate.computeSingleLayerGrid(configHash, sizeCmd);
        }

        @Override
        public List<NormalizedNoiseGrid> computeMultiLayerGrids(int configHash, ImageRequestCmd.SizeCmd sizeCmd) {
            count.incrementAndGet();
            return delegate.computeMultiLayerGrids(configHash, sizeCmd);
        }

        int computeCount() {
            return count.get();
        }
    }
}
