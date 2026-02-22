package org.dbs.sbgb.exposition.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dbs.sbgb.domain.model.GalaxyImage;
import org.dbs.sbgb.exposition.resources.dto.GalaxyImageDTO;
import org.dbs.sbgb.exposition.resources.mapper.MapperGalaxyImage;
import org.dbs.sbgb.port.in.BuildGalaxyImageUseCase;
import org.dbs.sbgb.port.in.CreateGalaxyImageUseCase;
import org.dbs.sbgb.port.in.FindGalaxyImagesUseCase;
import org.dbs.sbgb.port.in.GalaxyRequestCmd;
import org.dbs.sbgb.port.in.UpdateGalaxyNoteUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GalaxyResource.class)
class GalaxyResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BuildGalaxyImageUseCase buildGalaxyImageUseCase;

    @MockitoBean
    private CreateGalaxyImageUseCase createGalaxyImageUseCase;

    @MockitoBean
    private FindGalaxyImagesUseCase findGalaxyImagesUseCase;

    @MockitoBean
    private UpdateGalaxyNoteUseCase updateGalaxyNoteUseCase;

    @MockitoBean
    private MapperGalaxyImage mapperGalaxyImage;

    @Test
    void shouldReturnAllGalaxyImages() throws Exception {
        UUID id = UUID.randomUUID();
        GalaxyImage image = GalaxyImage.builder()
                .id(id)
                .description("A spiral galaxy")
                .note(3)
                .build();

        GalaxyImageDTO dto = new GalaxyImageDTO();
        dto.setId(id);
        dto.setDescription("A spiral galaxy");
        dto.setNote(3);

        when(findGalaxyImagesUseCase.findAllGalaxyImages()).thenReturn(List.of(image));
        when(mapperGalaxyImage.toDTO(image)).thenReturn(dto);

        mockMvc.perform(get("/galaxies").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("A spiral galaxy"))
                .andExpect(jsonPath("$[0].id").value(id.toString()));
    }

    @Test
    void shouldBuildGalaxyImage() throws Exception {
        GalaxyRequestCmd cmd = GalaxyRequestCmd.builder()
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
        UUID id = UUID.randomUUID();
        GalaxyRequestCmd cmd = GalaxyRequestCmd.builder()
                .width(500)
                .height(500)
                .note(4)
                .build();

        GalaxyImage image = GalaxyImage.builder()
                .id(id)
                .description("SPIRAL galaxy, 500x500px, CLASSIC palette, seed 0")
                .note(4)
                .build();

        GalaxyImageDTO dto = new GalaxyImageDTO();
        dto.setId(id);
        dto.setNote(4);

        when(createGalaxyImageUseCase.createGalaxyImage(any(GalaxyRequestCmd.class))).thenReturn(image);
        when(mapperGalaxyImage.toDTO(image)).thenReturn(dto);

        mockMvc.perform(post("/galaxies/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cmd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.note").value(4))
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void shouldUpdateNote() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(patch("/galaxies/{id}/note", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"note\": 4}"))
                .andExpect(status().isNoContent());

        verify(updateGalaxyNoteUseCase).updateNote(id, 4);
    }

    @Test
    void shouldReturnBadRequestWhenInvalidWidth() throws Exception {
        GalaxyRequestCmd invalidCmd = GalaxyRequestCmd.builder()
                .width(10) // Invalid width (min 100)
                .height(500)
                .build();

        mockMvc.perform(post("/galaxies/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCmd)))
                .andExpect(status().isBadRequest());
    }
}
