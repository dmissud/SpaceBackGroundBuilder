package org.dbs.sbgb.exposition.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.exposition.common.LogExecutionTime;
import org.dbs.sbgb.exposition.resources.dto.GalaxyBaseStructureDTO;
import org.dbs.sbgb.exposition.resources.dto.GalaxyCosmeticRenderDTO;
import org.dbs.sbgb.exposition.resources.mapper.GalaxyBaseStructureDTOMapper;
import org.dbs.sbgb.exposition.resources.mapper.GalaxyCosmeticRenderDTOMapper;
import org.dbs.sbgb.port.in.*;
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
@RequestMapping("/galaxy")
public class GalaxyResource {

    private final BuildGalaxyImageUseCase buildGalaxyImageUseCase;
    private final RateGalaxyCosmeticRenderUseCase rateUseCase;
    private final FindGalaxyBaseStructuresUseCase findBasesUseCase;
    private final FindGalaxyCosmeticRendersUseCase findRendersUseCase;
    private final DeleteGalaxyCosmeticRenderUseCase deleteRenderUseCase;
    private final DeleteRendersByBaseUseCase deleteRendersByBaseUseCase;
    private final ReapplyGalaxyCosmeticsUseCase reapplyUseCase;
    private final ResolveGalaxyBaseUseCase resolveBaseUseCase;
    private final GalaxyBaseStructureDTOMapper baseMapper;
    private final GalaxyCosmeticRenderDTOMapper renderMapper;

    @PostMapping(value = "/build", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(
            description = "Generate a galaxy image without saving",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = GalaxyRequestCmd.class)),
                    description = "Galaxy generation parameters"))
    @LogExecutionTime
    public ResponseEntity<byte[]> buildGalaxy(@Valid @RequestBody GalaxyRequestCmd cmd) throws IOException {
        return ResponseEntity.ok(buildGalaxyImageUseCase.buildGalaxyImage(cmd));
    }

    @PostMapping(value = "/renders/rate", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            description = "Rate a galaxy render — creates or updates Base and Render",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = GalaxyRequestCmd.class)),
                    description = "Galaxy parameters + note (1-5)"))
    @LogExecutionTime
    public ResponseEntity<GalaxyCosmeticRenderDTO> rateRender(@Valid @RequestBody GalaxyRequestCmd cmd) throws IOException {
        return ResponseEntity.status(201).body(renderMapper.toDTO(rateUseCase.rate(cmd)));
    }

    @GetMapping(value = "/bases", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "List all saved galaxy base structures, sorted by best note descending")
    @LogExecutionTime
    public ResponseEntity<List<GalaxyBaseStructureDTO>> getBases() {
        return ResponseEntity.ok(
                findBasesUseCase.findAllSortedByMaxNoteDesc().stream().map(baseMapper::toDTO).toList()
        );
    }

    @GetMapping(value = "/bases/{id}/renders", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "List all cosmetic renders for a given galaxy base structure")
    @LogExecutionTime
    public ResponseEntity<List<GalaxyCosmeticRenderDTO>> getRendersForBase(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(
                findRendersUseCase.findRendersByBaseId(id).stream().map(renderMapper::toDTO).toList()
        );
    }

    @DeleteMapping(value = "/renders/{id}")
    @Operation(description = "Delete a cosmetic render (and its base structure if it becomes orphan)")
    @LogExecutionTime
    public ResponseEntity<Void> deleteRender(@PathVariable("id") UUID id) {
        deleteRenderUseCase.deleteRender(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/bases/{id}/renders")
    @Operation(description = "Delete all cosmetic renders for a base and the base itself")
    @LogExecutionTime
    public ResponseEntity<Void> deleteRendersByBase(@PathVariable("id") UUID id) {
        deleteRendersByBaseUseCase.deleteRendersByBase(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/bases/resolve", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Find an existing galaxy base structure matching the given parameters (by config hash)")
    @LogExecutionTime
    public ResponseEntity<GalaxyBaseStructureDTO> resolveBase(@Valid @RequestBody GalaxyRequestCmd cmd) {
        return resolveBaseUseCase.resolveBase(cmd)
                .map(base -> ResponseEntity.ok(baseMapper.toDTO(base)))
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping(value = "/bases/{id}/reapply", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Reapply cosmetics to all renders of a base after structural change")
    @LogExecutionTime
    public ResponseEntity<List<GalaxyCosmeticRenderDTO>> reapplyCosmetics(
            @PathVariable("id") UUID id,
            @Valid @RequestBody GalaxyRequestCmd cmd) throws IOException {
        return ResponseEntity.ok(
                reapplyUseCase.reapplyCosmetics(id, cmd).stream()
                        .map(renderMapper::toDTO)
                        .toList()
        );
    }
}
