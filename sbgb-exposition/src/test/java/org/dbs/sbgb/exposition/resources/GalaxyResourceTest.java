package org.dbs.sbgb.exposition.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dbs.sbgb.domain.model.GalaxyBaseStructure;
import org.dbs.sbgb.domain.model.GalaxyCosmeticRender;
import org.dbs.sbgb.exposition.resources.dto.GalaxyBaseStructureDTO;
import org.dbs.sbgb.exposition.resources.dto.GalaxyCosmeticRenderDTO;
import org.dbs.sbgb.exposition.resources.mapper.GalaxyBaseStructureDTOMapper;
import org.dbs.sbgb.exposition.resources.mapper.GalaxyCosmeticRenderDTOMapper;
import org.dbs.sbgb.port.in.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private RateGalaxyCosmeticRenderUseCase rateUseCase;

    @MockitoBean
    private FindGalaxyBaseStructuresUseCase findBasesUseCase;

    @MockitoBean
    private FindGalaxyCosmeticRendersUseCase findRendersUseCase;

    @MockitoBean
    private DeleteGalaxyCosmeticRenderUseCase deleteRenderUseCase;

    @MockitoBean
    private DeleteRendersByBaseUseCase deleteRendersByBaseUseCase;

    @MockitoBean
    private ReapplyGalaxyCosmeticsUseCase reapplyUseCase;

    @MockitoBean
    private ResolveGalaxyBaseUseCase resolveBaseUseCase;

    @MockitoBean
    private GalaxyBaseStructureDTOMapper baseMapper;

    @MockitoBean
    private GalaxyCosmeticRenderDTOMapper renderMapper;

    @Test
    void shouldBuildGalaxyImage() throws Exception {
        GalaxyRequestCmd cmd = GalaxyRequestCmd.builder().width(500).height(500).build();
        when(buildGalaxyImageUseCase.buildGalaxyImage(any())).thenReturn(new byte[]{1, 2, 3});

        mockMvc.perform(post("/galaxy/build")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cmd)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(new byte[]{1, 2, 3}));
    }

    @Test
    void shouldRateRenderAndReturn201() throws Exception {
        UUID renderId = UUID.randomUUID();
        GalaxyRequestCmd cmd = GalaxyRequestCmd.builder().width(500).height(500).note(3).build();
        GalaxyCosmeticRender render = buildRender(renderId);
        GalaxyCosmeticRenderDTO dto = buildRenderDTO(renderId);

        when(rateUseCase.rate(any())).thenReturn(render);
        when(renderMapper.toDTO(render)).thenReturn(dto);

        mockMvc.perform(post("/galaxy/renders/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cmd)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(renderId.toString()))
                .andExpect(jsonPath("$.note").value(3));
    }

    @Test
    void shouldReturnAllBases() throws Exception {
        UUID baseId = UUID.randomUUID();
        GalaxyBaseStructure base = buildBase(baseId);
        GalaxyBaseStructureDTO dto = buildBaseDTO(baseId);

        when(findBasesUseCase.findAllSortedByMaxNoteDesc()).thenReturn(List.of(base));
        when(baseMapper.toDTO(base)).thenReturn(dto);

        mockMvc.perform(get("/galaxy/bases").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(baseId.toString()))
                .andExpect(jsonPath("$[0].description").value("Spirale — 500x500, seed 42"));
    }

    @Test
    void shouldReturnRendersForBase() throws Exception {
        UUID baseId = UUID.randomUUID();
        UUID renderId = UUID.randomUUID();
        GalaxyCosmeticRender render = buildRender(renderId);
        GalaxyCosmeticRenderDTO dto = buildRenderDTO(renderId);

        when(findRendersUseCase.findRendersByBaseId(baseId)).thenReturn(List.of(render));
        when(renderMapper.toDTO(render)).thenReturn(dto);

        mockMvc.perform(get("/galaxy/bases/{id}/renders", baseId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(renderId.toString()));
    }

    @Test
    void shouldDeleteRenderAndReturn204() throws Exception {
        UUID renderId = UUID.randomUUID();

        mockMvc.perform(delete("/galaxy/renders/{id}", renderId))
                .andExpect(status().isNoContent());

        verify(deleteRenderUseCase).deleteRender(eq(renderId));
    }

    private GalaxyBaseStructure buildBase(UUID id) {
        return new GalaxyBaseStructure(id, "Spirale — 500x500, seed 42", 3,
                500, 500, 42L, "SPIRAL", 0.1, 200.0, 0.0,
                4, 0.5, 2.0, 100.0, false,
                1.0, 0.5, 0.5, 0.3, 0.1, 0.2, "{}",
                2, 80.0, 4.0, 0.0,
                null, null, null,
                null, null, null,
                null, null, null, null,
                null, null, null);
    }

    private GalaxyBaseStructureDTO buildBaseDTO(UUID id) {
        GalaxyBaseStructureDTO dto = new GalaxyBaseStructureDTO();
        dto.setId(id);
        dto.setDescription("Spirale — 500x500, seed 42");
        dto.setMaxNote(3);
        return dto;
    }

    private GalaxyCosmeticRender buildRender(UUID id) {
        return new GalaxyCosmeticRender(id, UUID.randomUUID(), "Classique", 3, new byte[0],
                "CLASSIC", "#000000", "#ffffff", "#aaaaaa", "#555555",
                false, 0.0, 0.0, 0.0,
                false, 0.0, 1.0, false, 0);
    }

    private GalaxyCosmeticRenderDTO buildRenderDTO(UUID id) {
        GalaxyCosmeticRenderDTO dto = new GalaxyCosmeticRenderDTO();
        dto.setId(id);
        dto.setNote(3);
        return dto;
    }
}
