package org.dbs.sbgb.exposition.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.exposition.common.LogExecutionTime;
import org.dbs.sbgb.exposition.resources.dto.NoiseCosmeticRenderDTO;
import org.dbs.sbgb.exposition.resources.dto.NoiseBaseStructureDTO;
import org.dbs.sbgb.exposition.resources.mapper.NoiseCosmeticRenderMapper;
import org.dbs.sbgb.exposition.resources.mapper.NoiseBaseStructureMapper;
import org.dbs.sbgb.port.in.BuildNoiseImageUseCase;
import org.dbs.sbgb.port.in.DeleteNoiseCosmeticRenderUseCase;
import org.dbs.sbgb.port.in.FindNoiseBaseStructuresUseCase;
import org.dbs.sbgb.port.in.ImageRequestCmd;
import org.dbs.sbgb.port.in.RateNoiseCosmeticRenderUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@AllArgsConstructor
@Validated
@RequestMapping("/")
public class ImageResource {

    private final BuildNoiseImageUseCase buildNoiseImageUseCase;
    private final RateNoiseCosmeticRenderUseCase rateNoiseCosmeticRenderUseCase;
    private final FindNoiseBaseStructuresUseCase findNoiseBaseStructuresUseCase;
    private final DeleteNoiseCosmeticRenderUseCase deleteNoiseCosmeticRenderUseCase;
    private final NoiseBaseStructureMapper baseStructureMapper;
    private final NoiseCosmeticRenderMapper cosmeticRenderMapper;

    @PostMapping(value = "/images/build", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(
            description = "Build a noise image without saving",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = ImageRequestCmd.class))))
    @LogExecutionTime
    public ResponseEntity<byte[]> buildImage(@Valid @RequestBody ImageRequestCmd cmd) throws IOException {
        byte[] bytes = buildNoiseImageUseCase.buildNoiseImage(cmd);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping(value = "/images/renders/rate", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            description = "Rate a cosmetic render — creates base structure and render if not found",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = ImageRequestCmd.class))))
    @LogExecutionTime
    public ResponseEntity<NoiseCosmeticRenderDTO> rateRender(@Valid @RequestBody ImageRequestCmd cmd) throws IOException {
        var render = rateNoiseCosmeticRenderUseCase.rate(cmd);
        return ResponseEntity.status(201).body(cosmeticRenderMapper.toDTO(render));
    }

    @GetMapping(value = "/images/bases", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Get all noise base structures sorted by max note descending")
    @LogExecutionTime
    public ResponseEntity<List<NoiseBaseStructureDTO>> getBases() {
        List<NoiseBaseStructureDTO> dtos = findNoiseBaseStructuresUseCase.findAllSortedByMaxNoteDesc()
                .stream()
                .map(baseStructureMapper::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping(value = "/images/renders/{id}")
    @Operation(description = "Delete a cosmetic render — also deletes base if it has no more renders")
    @LogExecutionTime
    public ResponseEntity<Void> deleteRender(@PathVariable UUID id) {
        deleteNoiseCosmeticRenderUseCase.deleteRender(id);
        return ResponseEntity.noContent().build();
    }
}
