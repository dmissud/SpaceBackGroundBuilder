package org.dbs.spgb.spgbexposition.resources;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dbs.spgb.port.in.BuildNoiseImageUseCase;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@Slf4j
@AllArgsConstructor
@Validated
public class ImageResource {

    private final BuildNoiseImageUseCase buildNoiseImageUseCase;

    @PostMapping(value = "images", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(description = "Create a image", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody())
    public Mono<byte[]> postTestDto(@Valid @RequestBody final BuildNoiseImageUseCase.ImageRequestCmd imageRequestCmd, final ServerWebExchange exchange) {

        byte[] bytes;
        try {
            bytes = buildNoiseImageUseCase.buildNoiseImage(imageRequestCmd);
        } catch (IOException e) {
            log.error("Erreur de génération de l'image", e);
            return Mono.error(e);
        }

        return Mono.just(bytes);
    }

}