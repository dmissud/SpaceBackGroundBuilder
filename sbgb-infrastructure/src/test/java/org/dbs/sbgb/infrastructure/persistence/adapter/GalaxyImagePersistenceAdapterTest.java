package org.dbs.sbgb.infrastructure.persistence.adapter;

import org.dbs.sbgb.domain.model.GalaxyImage;
import org.dbs.sbgb.domain.model.GalaxyStructure;
import org.dbs.sbgb.infrastructure.persistence.jpa.GalaxyImageJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@DataJpaTest
@EntityScan("org.dbs.sbgb.domain.model")
class GalaxyImagePersistenceAdapterTest {

    @Autowired
    private GalaxyImageJpaRepository jpaRepository;

    private GalaxyImagePersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new GalaxyImagePersistenceAdapter(jpaRepository);
    }

    @Test
    void shouldSaveAndRetrieveGalaxyImageWithFullStructure() {
        // Arrange
        UUID id = UUID.randomUUID();
        GalaxyStructure structure = GalaxyStructure.builder()
                .width(1000)
                .height(1000)
                .seed(12345L)
                .galaxyType("SPIRAL")
                .numberOfArms(3)
                .armWidth(0.5)
                .armRotation(2.0)
                .coreSize(0.1)
                .galaxyRadius(400.0)
                .noiseOctaves(4)
                .noisePersistence(0.5)
                .noiseLacunarity(2.0)
                .noiseScale(0.1)
                .warpStrength(50.0)
                .colorPalette("NEBULA")
                .starDensity(0.005)
                .maxStarSize(3)
                .diffractionSpikes(true)
                .spikeCount(4)
                .build();

        GalaxyImage image = GalaxyImage.builder()
                .id(id)
                .name("Test Spiral")
                .description("A test spiral galaxy")
                .note(1)
                .galaxyStructure(structure)
                .image(new byte[] { 1, 2, 3, 4 })
                .build();

        // Act
        GalaxyImage savedImage = adapter.save(image);
        Optional<GalaxyImage> retrievedOpt = adapter.findByName("Test Spiral");

        // Assert
        assertThat(retrievedOpt).isPresent();
        GalaxyImage retrieved = retrievedOpt.get();
        assertThat(retrieved.getId()).isEqualTo(id);
        assertThat(retrieved.getName()).isEqualTo("Test Spiral");
        assertThat(retrieved.getImage()).containsExactly(1, 2, 3, 4);

        GalaxyStructure retrievedStruct = retrieved.getGalaxyStructure();
        assertThat(retrievedStruct).isNotNull();
        assertThat(retrievedStruct.getWidth()).isEqualTo(1000);
        assertThat(retrievedStruct.getGalaxyType()).isEqualTo("SPIRAL");
        assertThat(retrievedStruct.getNumberOfArms()).isEqualTo(3);
        assertThat(retrievedStruct.getColorPalette()).isEqualTo("NEBULA");
        assertThat(retrievedStruct.getWarpStrength()).isEqualTo(50.0);
        assertThat(retrievedStruct.isDiffractionSpikes()).isTrue();
    }
}
