package org.dbs.spgb.spgbexposition.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dbs.spgb.port.in.BuildNoiseImageUseCase;
import org.dbs.spgb.spgbexposition.common.LogExecutionTime;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * Rest Controller for managing Images
 */
@RestController
@Slf4j
@AllArgsConstructor
@Validated
public class ImageResource {
    // Colors used for image generation
    private static final String BLACK = "#000000";
    private static final String ORANGE = "#FFA500";
    private static final String WHITE = "#FFFFF";

    // Thresholds used for image generation
    public static final double BACKGROUND_THRESHOLD = 0.7;
    public static final double MIDCOLOR_THRESHOLD = 0.75;

    // Use Case for building noise image
    private final BuildNoiseImageUseCase buildNoiseImageUseCase;

    /**
     * Rest Endpoint for creating an image
     *
     * @param imageRequestCmd The image parameters
     * @return Mono<byte [ ]> The generated image
     */
    @PostMapping(value = "images", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(
            description = "Create an image",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = BuildNoiseImageUseCase.ImageRequestCmd.class)),
                    description = "The parameters for new image generation. It includes the size of the image (height, width, seed) and colors for the back, middle and front parts of the image with respective threshold values"))
    @LogExecutionTime
    public Mono<byte[]> buildImage(@Valid @RequestBody final BuildNoiseImageUseCase.ImageRequestCmd imageRequestCmd) {

        // Default colors if none are supplied
        if (imageRequestCmd.getColorCmd() == null) {
            imageRequestCmd.setColorCmd(buildColorCmd());
        }

        // Execute the use case
        byte[] bytes;
        try {
            bytes = buildNoiseImageUseCase.buildNoiseImage(imageRequestCmd);
        } catch (IOException e) {
            log.error("Erreur de génération de l'image", e);
            return Mono.error(e);
        }

        // Return the image
        return Mono.just(bytes);
    }

    /**
     * Helper function to build default color command
     *
     * @return ColorCmd the default color command
     */
    private static BuildNoiseImageUseCase.ImageRequestCmd.ColorCmd buildColorCmd() {
        BuildNoiseImageUseCase.ImageRequestCmd.ColorCmd colorCmd = new BuildNoiseImageUseCase.ImageRequestCmd.ColorCmd();
        colorCmd.setBack(BLACK);
        colorCmd.setMiddle(ORANGE);
        colorCmd.setFore(WHITE);
        colorCmd.setBackThreshold(BACKGROUND_THRESHOLD);
        colorCmd.setMiddleThreshold(MIDCOLOR_THRESHOLD);
        return colorCmd;
    }

}