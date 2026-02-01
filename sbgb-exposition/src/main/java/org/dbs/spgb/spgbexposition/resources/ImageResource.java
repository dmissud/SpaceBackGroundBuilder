package org.dbs.spgb.spgbexposition.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dbs.spgb.domain.model.NoiseImage;
import org.dbs.spgb.port.in.BuildNoiseImageUseCase;
import org.dbs.spgb.port.in.CreateNoiseImageUseCase;
import org.dbs.spgb.port.in.ImageRequestCmd;
import org.dbs.spgb.spgbexposition.common.LogExecutionTime;
import org.dbs.spgb.spgbexposition.resources.dto.NoiseImageDTO;
import org.dbs.spgb.spgbexposition.resources.mapper.MapperNoiseImage;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
@AllArgsConstructor
@Validated
@RequestMapping("/")
public class ImageResource {
    private final BuildNoiseImageUseCase buildNoiseImageUseCase;
    private final CreateNoiseImageUseCase createNoiseImageUseCase;
    private final MapperNoiseImage mapperNoiseImage;
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