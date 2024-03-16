package org.dbs.spgb.spgbexposition.resources;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dbs.spgb.port.in.BuildNoiseImageUseCase;
import org.dbs.spgb.spgbexposition.common.LogExecutionTime;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.awt.*;
import java.io.IOException;

@RestController
@Slf4j
@AllArgsConstructor
@Validated
public class ImageResource {
    private static final Color BLACK = new Color(0, 0, 0);
    private static final Color ORANGE = new Color(255, 165, 0);
    private static final Color WHITE = new Color(255, 255, 255);
    public static final double BACKGROUND_THRESHOLD = 0.7;
    public static final double MIDCOLOR_THRESHOLD = 0.75;

    private final BuildNoiseImageUseCase buildNoiseImageUseCase;

    @PostMapping(value = "images", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(description = "Create a image", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody())
    @LogExecutionTime
    public Mono<byte[]> buildImage(@Valid @RequestBody final BuildNoiseImageUseCase.ImageRequestCmd imageRequestCmd, final ServerWebExchange exchange) {

        if (imageRequestCmd.getColorCmd() == null) {
            imageRequestCmd.setColorCmd(buildColorCmd());
        }

        byte[] bytes;
        try {
            bytes = buildNoiseImageUseCase.buildNoiseImage(imageRequestCmd);
        } catch (IOException e) {
            log.error("Erreur de génération de l'image", e);
            return Mono.error(e);
        }

        return Mono.just(bytes);
    }

    private static BuildNoiseImageUseCase.ImageRequestCmd.ColorCmd buildColorCmd() {
        BuildNoiseImageUseCase.ImageRequestCmd.ColorCmd colorCmd = new BuildNoiseImageUseCase.ImageRequestCmd.ColorCmd();
        colorCmd.setBack(BLACK);
        colorCmd.setMiddle(ORANGE);
        colorCmd.setFront(WHITE);
        colorCmd.setBackTreshold(BACKGROUND_THRESHOLD);
        colorCmd.setMiddleTreshold(MIDCOLOR_THRESHOLD);
        return colorCmd;
    }

}