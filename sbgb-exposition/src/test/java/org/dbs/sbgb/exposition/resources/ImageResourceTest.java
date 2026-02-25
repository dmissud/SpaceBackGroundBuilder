package org.dbs.sbgb.exposition.resources;

import org.dbs.sbgb.domain.model.NoiseCosmeticRender;
import org.dbs.sbgb.exposition.resources.mapper.NoiseCosmeticRenderMapper;
import org.dbs.sbgb.exposition.resources.mapper.NoiseBaseStructureMapper;
import org.dbs.sbgb.port.in.*;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ImageResource.class)
class ImageResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BuildNoiseImageUseCase buildNoiseImageUseCase;
    @MockitoBean
    private RateNoiseCosmeticRenderUseCase rateNoiseCosmeticRenderUseCase;
    @MockitoBean
    private FindNoiseBaseStructuresUseCase findNoiseBaseStructuresUseCase;
    @MockitoBean
    private DeleteNoiseCosmeticRenderUseCase deleteNoiseCosmeticRenderUseCase;
    @MockitoBean
    private FindNoiseCosmeticRendersUseCase findNoiseCosmeticRendersUseCase;
    @MockitoBean
    private NoiseBaseStructureMapper baseStructureMapper;
    @MockitoBean
    private NoiseCosmeticRenderMapper cosmeticRenderMapper;

    @Test
    void shouldReturnRendersForBase() throws Exception {
        UUID baseId = UUID.randomUUID();
        UUID renderId = UUID.randomUUID();
        NoiseCosmeticRender render = new NoiseCosmeticRender(renderId, baseId,
                "#000000", "#888888", "#FFFFFF", 0.4, 0.7, "LINEAR", false, 3, null, "desc");

        when(findNoiseCosmeticRendersUseCase.findRendersByBaseId(baseId)).thenReturn(List.of(render));

        mockMvc.perform(get("/images/bases/{id}/renders", baseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnEmptyListWhenBaseHasNoRenders() throws Exception {
        UUID baseId = UUID.randomUUID();
        when(findNoiseCosmeticRendersUseCase.findRendersByBaseId(baseId)).thenReturn(List.of());

        mockMvc.perform(get("/images/bases/{id}/renders", baseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
