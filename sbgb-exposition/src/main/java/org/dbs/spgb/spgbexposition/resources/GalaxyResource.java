package org.dbs.spgb.spgbexposition.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dbs.spgb.domain.model.GalaxyImage;
import org.dbs.spgb.port.in.BuildGalaxyImageUseCase;
import org.dbs.spgb.port.in.CreateGalaxyImageUseCase;
import org.dbs.spgb.port.in.FindGalaxyImagesUseCase;
import org.dbs.spgb.port.in.GalaxyRequestCmd;
import org.dbs.spgb.spgbexposition.common.LogExecutionTime;
import org.dbs.spgb.spgbexposition.resources.dto.GalaxyImageDTO;
import org.dbs.spgb.spgbexposition.resources.mapper.MapperGalaxyImage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@AllArgsConstructor
@Validated
@RequestMapping("/galaxies")
public class GalaxyResource {
    private final BuildGalaxyImageUseCase buildGalaxyImageUseCase;
    private final CreateGalaxyImageUseCase createGalaxyImageUseCase;
    private final FindGalaxyImagesUseCase findGalaxyImagesUseCase;
    private final MapperGalaxyImage mapperGalaxyImage;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Get all saved galaxy images")
    @LogExecutionTime
    public ResponseEntity<List<GalaxyImageDTO>> getAllGalaxyImages() {
        List<GalaxyImage> images = findGalaxyImagesUseCase.findAllGalaxyImages();
        List<GalaxyImageDTO> dtos = images.stream()
                .map(mapperGalaxyImage::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping(value = "/build", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(
            description = "Generate a galaxy image without saving",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = GalaxyRequestCmd.class)),
                    description = "Galaxy generation parameters including spiral structure and noise texture settings"))
    @LogExecutionTime
    public ResponseEntity<byte[]> buildGalaxy(@Valid @RequestBody final GalaxyRequestCmd galaxyRequestCmd) throws IOException {
        byte[] bytes = buildGalaxyImageUseCase.buildGalaxyImage(galaxyRequestCmd);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            description = "Create and save a galaxy image",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = GalaxyRequestCmd.class)),
                    description = "Galaxy generation parameters including spiral structure and noise texture settings"))
    @LogExecutionTime
    public ResponseEntity<GalaxyImageDTO> createGalaxy(@Valid @RequestBody final GalaxyRequestCmd galaxyRequestCmd) throws IOException {
        GalaxyImage galaxyImage = createGalaxyImageUseCase.createGalaxyImage(galaxyRequestCmd);
        GalaxyImageDTO dto = mapperGalaxyImage.toDTO(galaxyImage);
        return ResponseEntity.ok(dto);
    }
}
