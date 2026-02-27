package org.dbs.sbgb.infrastructure.cache;

import org.dbs.sbgb.domain.model.NormalizedNoiseGrid;
import org.dbs.sbgb.port.in.ImageRequestCmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = CacheTestConfig.class)
class CachedNoiseGridAdapterTest {

    @Autowired
    private CachedNoiseGridAdapter adapter;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCache() {
        cacheManager.getCache("noiseGrid").clear();
    }

    @Test
    void shouldReturnValidGridForSingleLayer() {
        ImageRequestCmd.SizeCmd sizeCmd = buildSizeCmd();

        NormalizedNoiseGrid grid = adapter.computeSingleLayerGrid(12345, sizeCmd);

        assertThat(grid).isNotNull();
        assertThat(grid.width()).isEqualTo(50);
        assertThat(grid.height()).isEqualTo(50);
    }

    @Test
    void shouldReturnSameInstanceFromCacheOnSecondCall() {
        ImageRequestCmd.SizeCmd sizeCmd = buildSizeCmd();
        int configHash = 99999;

        NormalizedNoiseGrid first = adapter.computeSingleLayerGrid(configHash, sizeCmd);
        NormalizedNoiseGrid second = adapter.computeSingleLayerGrid(configHash, sizeCmd);

        assertThat(second).isSameAs(first);
    }

    @Test
    void shouldReturnDifferentGridForDifferentConfigHash() {
        ImageRequestCmd.SizeCmd sizeCmd = buildSizeCmd();

        NormalizedNoiseGrid grid1 = adapter.computeSingleLayerGrid(11111, sizeCmd);
        NormalizedNoiseGrid grid2 = adapter.computeSingleLayerGrid(22222, sizeCmd);

        assertThat(grid2).isNotSameAs(grid1);
    }

    @Test
    void shouldReturnNonEmptyMultiLayerGrids() {
        ImageRequestCmd.SizeCmd sizeCmd = buildSizeCmd();

        var grids = adapter.computeMultiLayerGrids(55555, sizeCmd);

        assertThat(grids).isNotEmpty();
    }

    private ImageRequestCmd.SizeCmd buildSizeCmd() {
        return ImageRequestCmd.SizeCmd.builder()
                .width(50).height(50).seed(42)
                .octaves(1).persistence(0.5).lacunarity(2.0).scale(100.0)
                .noiseType("FBM").useMultiLayer(false)
                .build();
    }
}
