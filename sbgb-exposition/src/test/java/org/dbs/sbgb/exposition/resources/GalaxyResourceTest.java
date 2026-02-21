package org.dbs.sbgb.exposition.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dbs.sbgb.domain.model.GalaxyImage;
import org.dbs.sbgb.exposition.resources.dto.GalaxyImageDTO;
import org.dbs.sbgb.exposition.resources.mapper.MapperGalaxyImage;
import org.dbs.sbgb.port.in.BuildGalaxyImageUseCase;
import org.dbs.sbgb.port.in.CreateGalaxyImageUseCase;
import org.dbs.sbgb.port.in.FindGalaxyImagesUseCase;
import org.dbs.sbgb.port.in.GalaxyRequestCmd;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GalaxyResource.class)
class GalaxyResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BuildGalaxyImageUseCase buildGalaxyImageUseCase;

    @MockBean
    private CreateGalaxyImageUseCase createGalaxyImageUseCase;

    @MockBean
    private FindGalaxyImagesUseCase findGalaxyImagesUseCase;

    @MockBean
    private MapperGalaxyImage mapperGalaxyImage;

    @Test
    void shouldReturnAllGalaxyImages() throws Exception {
        GalaxyImage image = GalaxyImage.builder()
                .id(UUID.randomUUID())
                .name("Test Galaxy")
                .build();

        GalaxyImageDTO dto = new GalaxyImageDTO();
        dto.setId(image.getId());
        dto.setName("Test Galaxy");

        when(findGalaxyImagesUseCase.findAllGalaxyImages()).thenReturn(List.of(image));
        when(mapperGalaxyImage.toDTO(image)).thenReturn(dto);

        mockMvc.perform(get("/galaxies")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Galaxy"))
                .andExpect(jsonPath("$[0].id").value(image.getId().toString()));
    }

    @Test
    void shouldBuildGalaxyImage() throws Exception {
        GalaxyRequestCmd cmd = GalaxyRequestCmd.builder()
                .name("Build Test")
                .width(500)
                .height(500)
                .build();

        when(buildGalaxyImageUseCase.buildGalaxyImage(any(GalaxyRequestCmd.class)))
                .thenReturn(new byte[] { 1, 2, 3 });

        mockMvc.perform(post("/galaxies/build")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cmd)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(new byte[] { 1, 2, 3 }));
    }

    @Test
    void shouldCreateGalaxyImage() throws Exception {
        GalaxyRequestCmd cmd = GalaxyRequestCmd.builder()
                .name("Create Test")
                .width(500)
                .height(500)
                .build();

        GalaxyImage image = GalaxyImage.builder()
                .id(UUID.randomUUID())
                .name("Create Test")
                .build();

        GalaxyImageDTO dto = new GalaxyImageDTO();
        dto.setId(image.getId());
        dto.setName("Create Test");

        when(createGalaxyImageUseCase.createGalaxyImage(any(GalaxyRequestCmd.class))).thenReturn(image);
        when(mapperGalaxyImage.toDTO(image)).thenReturn(dto);

        mockMvc.perform(post("/galaxies/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cmd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Create Test"))
                .andExpect(jsonPath("$.id").value(image.getId().toString()));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidRequest() throws Exception {
        GalaxyRequestCmd invalidCmd = GalaxyRequestCmd.builder()
                .name("Invalid Test")
                .width(10) // Invalid width (min 100)
                .height(500)
                .build();

        mockMvc.perform(post("/galaxies/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCmd)))
                .andExpect(status().isBadRequest());
    }
}
