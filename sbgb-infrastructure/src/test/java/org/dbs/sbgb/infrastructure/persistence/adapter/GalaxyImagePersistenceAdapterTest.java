package org.dbs.sbgb.infrastructure.persistence.adapter;

import org.dbs.sbgb.domain.model.GalaxyImage;
import org.dbs.sbgb.domain.model.GalaxyStructure;
import org.dbs.sbgb.infrastructure.persistence.jpa.GalaxyImageJpaRepository;
import org.dbs.sbgb.infrastructure.persistence.mapper.GalaxyEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@DataJpaTest
@EntityScan("org.dbs.sbgb.infrastructure.persistence.entity")
class GalaxyImagePersistenceAdapterTest {

        @Autowired
        private GalaxyImageJpaRepository jpaRepository;

        private GalaxyImagePersistenceAdapter adapter;
        private GalaxyEntityMapper mapper;

        @BeforeEach
        void setUp() {
                mapper = Mappers.getMapper(GalaxyEntityMapper.class);
                adapter = new GalaxyImagePersistenceAdapter(jpaRepository, mapper);
        }

        @Test
        void shouldSaveAndRetrieveGalaxyImageById() {
                UUID id = UUID.randomUUID();
                GalaxyStructure structure = GalaxyStructure.builder()
                                .width(1000)
                                .height(1000)
                                .seed(12345L)
                                .galaxyType("SPIRAL")
                                .spiralStructure(org.dbs.sbgb.domain.model.vo.SpiralStructure.builder()
                                                .numberOfArms(3)
                                                .armWidth(0.5)
                                                .armRotation(2.0)
                                                .coreSize(0.1)
                                                .galaxyRadius(400.0)
                                                .build())
                                .noiseTexture(org.dbs.sbgb.domain.model.vo.NoiseTexture.builder()
                                                .noiseOctaves(4)
                                                .noisePersistence(0.5)
                                                .noiseLacunarity(2.0)
                                                .noiseScale(0.1)
                                                .build())
                                .warpStrength(50.0)
                                .colorConfig(org.dbs.sbgb.domain.model.vo.ColorConfig.builder()
                                                .colorPalette("NEBULA")
                                                .build())
                                .starField(org.dbs.sbgb.domain.model.vo.StarField.builder()
                                                .starDensity(0.005)
                                                .maxStarSize(3)
                                                .diffractionSpikes(true)
                                                .spikeCount(4)
                                                .build())
                                .build();

                GalaxyImage image = GalaxyImage.builder()
                                .id(id)
                                .description("A test spiral galaxy")
                                .note(3)
                                .galaxyStructure(structure)
                                .image(new byte[] { 1, 2, 3, 4 })
                                .build();

                adapter.save(image);
                GalaxyImage retrieved = adapter.findById(id);

                assertThat(retrieved).isNotNull();
                assertThat(retrieved.getId()).isEqualTo(id);
                assertThat(retrieved.getDescription()).isEqualTo("A test spiral galaxy");
                assertThat(retrieved.getNote()).isEqualTo(3);
                assertThat(retrieved.getImage()).containsExactly(1, 2, 3, 4);

                GalaxyStructure retrievedStruct = retrieved.getGalaxyStructure();
                assertThat(retrievedStruct).isNotNull();
                assertThat(retrievedStruct.getWidth()).isEqualTo(1000);
                assertThat(retrievedStruct.getGalaxyType()).isEqualTo("SPIRAL");
                assertThat(retrievedStruct.getSpiralStructure().getNumberOfArms()).isEqualTo(3);
                assertThat(retrievedStruct.getColorConfig().getColorPalette()).isEqualTo("NEBULA");
                assertThat(retrievedStruct.getWarpStrength()).isEqualTo(50.0);
                assertThat(retrievedStruct.getStarField().isDiffractionSpikes()).isTrue();
        }

        @Test
        void shouldSaveAndRetrieveBloomConfig() {
                UUID id = UUID.randomUUID();
                GalaxyStructure structure = GalaxyStructure.builder()
                                .width(500).height(500).seed(1L).galaxyType("SPIRAL")
                                .bloomConfig(org.dbs.sbgb.domain.model.vo.BloomConfig.builder()
                                                .bloomEnabled(true)
                                                .bloomRadius(15)
                                                .bloomIntensity(0.7)
                                                .bloomThreshold(0.4)
                                                .build())
                                .build();

                GalaxyImage image = GalaxyImage.builder()
                                .id(id)
                                .description("Bloom test galaxy")
                                .note(2)
                                .galaxyStructure(structure)
                                .image(new byte[] { 1 })
                                .build();

                adapter.save(image);
                GalaxyImage retrieved = adapter.findById(id);

                assertThat(retrieved.getGalaxyStructure().getBloomConfig()).isNotNull();
                assertThat(retrieved.getGalaxyStructure().getBloomConfig().isBloomEnabled()).isTrue();
                assertThat(retrieved.getGalaxyStructure().getBloomConfig().getBloomRadius()).isEqualTo(15);
                assertThat(retrieved.getGalaxyStructure().getBloomConfig().getBloomIntensity()).isEqualTo(0.7);
        }

        @Test
        void shouldUpdateNoteWithoutChangingOtherFields() {
                UUID id = UUID.randomUUID();
                GalaxyImage image = GalaxyImage.builder()
                                .id(id)
                                .description("Galaxy to rate")
                                .note(1)
                                .galaxyStructure(GalaxyStructure.builder()
                                                .width(100).height(100).seed(1L).galaxyType("SPIRAL")
                                                .build())
                                .image(new byte[] { 5, 6 })
                                .build();

                adapter.save(image);
                adapter.updateNote(id, 5);
                GalaxyImage updated = adapter.findById(id);

                assertThat(updated.getNote()).isEqualTo(5);
                assertThat(updated.getDescription()).isEqualTo("Galaxy to rate");
        }
}
