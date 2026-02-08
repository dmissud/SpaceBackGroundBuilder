package org.dbs.sbgb.spgbexposition.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.model.NoiseImage;
import org.dbs.sbgb.port.in.BuildNoiseImageUseCase;
import org.dbs.sbgb.port.in.CreateNoiseImageUseCase;
import org.dbs.sbgb.port.in.FindNoiseImagesUseCase;
import org.dbs.sbgb.port.in.ImageRequestCmd;
import org.dbs.sbgb.spgbexposition.common.LogExecutionTime;
import org.dbs.sbgb.spgbexposition.resources.dto.NoiseImageDTO;
import org.dbs.sbgb.spgbexposition.resources.mapper.MapperNoiseImage;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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
@RequestMapping("/")
public class ImageResource {
    private final BuildNoiseImageUseCase buildNoiseImageUseCase;
    private final CreateNoiseImageUseCase createNoiseImageUseCase;
    private final FindNoiseImagesUseCase findNoiseImagesUseCase;
    private final MapperNoiseImage mapperNoiseImage;

    @GetMapping(value = "/images", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Get all saved images")
    @LogExecutionTime
    public ResponseEntity<List<NoiseImageDTO>> getAllImages() {
        List<NoiseImage> images = findNoiseImagesUseCase.findAll();
        List<NoiseImageDTO> dtos = images.stream()
                .map(mapperNoiseImage::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping(value = "/images/build", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(
            description = "Create an image",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = ImageRequestCmd.class)),
                    description = "The parameters for new image generation. It includes the size of the image (height, width, seed) and colors for the back, middle and front parts of the image with respective threshold values"))
    @LogExecutionTime
    public ResponseEntity<byte[]> buildImage(@Valid @RequestBody final ImageRequestCmd imageRequestCmd) throws IOException {
        byte[] bytes = buildNoiseImageUseCase.buildNoiseImage(imageRequestCmd);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping(value = "/images/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            description = "Create an image",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = ImageRequestCmd.class)),
                    description = "The parameters for new image generation. It includes the size of the image (height, width, seed) and colors for the back, middle and front parts of the image with respective threshold values"))
    @LogExecutionTime
    public ResponseEntity<NoiseImageDTO> createImage(@Valid @RequestBody final ImageRequestCmd imageRequestCmd) throws IOException {
        NoiseImage noiseImage = createNoiseImageUseCase.createNoiseImage(imageRequestCmd);

        NoiseImageDTO noiseImageDTO = mapperNoiseImage.toDTO(noiseImage);

        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass())
                .createImage(imageRequestCmd)).withSelfRel();

        noiseImageDTO.get_links().put("self", selfLink);

        return ResponseEntity.created(selfLink.toUri())
                .body(noiseImageDTO);
    }
}