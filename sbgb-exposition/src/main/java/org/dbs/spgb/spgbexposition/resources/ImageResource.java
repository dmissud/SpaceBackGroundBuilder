package org.dbs.spgb.spgbexposition.resources;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.dbs.spgb.domain.SpaceBackGroundFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
public class ImageResource {

    @PostMapping("images")
    @Operation(description = "Create a image", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody())
    public Mono<Resource> postTestDto(@Valid @RequestBody final ImageRequestBody imageRequestBody, final ServerWebExchange exchange) {

        SpaceBackGroundFactory spaceBackGroundFactory = new SpaceBackGroundFactory.Builder()
                .withHeight(imageRequestBody.getHeight())
                .withWidth(imageRequestBody.getWidth())
                .build();

        BufferedImage image = spaceBackGroundFactory.create(imageRequestBody.getSeed());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return Mono.error(e);
        }
        byte[] bytes = outputStream.toByteArray();
        ByteArrayResource resource = new ByteArrayResource(bytes);

        return Mono.just(resource);
    }

    @Getter
    @Setter
    public static class ImageRequestBody {
        private int height;
        private int width;
        private int seed;
    }
}