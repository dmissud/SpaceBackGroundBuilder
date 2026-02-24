package org.dbs.sbgb.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.StarFieldGenerator;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

/**
 * Service responsible for applying star field overlay to galaxy images.
 * Encapsulates the logic of star field generation and application.
 */
@Slf4j
@Component
public class StarFieldApplicator {

    /**
     * Apply star field to the given image if enabled in parameters.
     *
     * @param image      base galaxy image
     * @param parameters galaxy parameters containing star field configuration
     * @param seed       random seed for star generation
     * @return image with star field applied, or original image if star field is
     *         disabled
     */
    public BufferedImage applyIfEnabled(BufferedImage image, GalaxyParameters parameters, long seed) {
        if (!isStarFieldEnabled(parameters)) {
            log.debug("Star field disabled (density={})", parameters.getStarFieldParameters().getStarDensity());
            return image;
        }

        return applyStarField(image, parameters, seed);
    }

    private boolean isStarFieldEnabled(GalaxyParameters parameters) {
        return parameters.getStarFieldParameters().isEnabled();
    }

    private BufferedImage applyStarField(BufferedImage image, GalaxyParameters parameters, long seed) {
        log.info("Applying star field: density={}, maxSize={}, spikes={}",
                parameters.getStarFieldParameters().getStarDensity(),
                parameters.getStarFieldParameters().getMaxStarSize(),
                parameters.getStarFieldParameters().isDiffractionSpikes());

        StarFieldGenerator generator = StarFieldGenerator.builder()
                .width(image.getWidth())
                .height(image.getHeight())
                .starDensity(parameters.getStarFieldParameters().getStarDensity())
                .maxStarSize(parameters.getStarFieldParameters().getMaxStarSize())
                .diffractionSpikes(parameters.getStarFieldParameters().isDiffractionSpikes())
                .spikeCount(parameters.getStarFieldParameters().getSpikeCount())
                .seed(seed + 999999) // Different seed offset for stars
                .build();

        return generator.applyStarField(image);
    }
}
