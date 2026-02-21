package org.dbs.sbgb.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.model.BloomPostProcessor;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.parameters.BloomParameters;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

/**
 * Service responsible for applying bloom/glow post-processing to galaxy images.
 */
@Slf4j
@Component
public class BloomApplicator {

    public BufferedImage applyIfEnabled(BufferedImage image, GalaxyParameters parameters) {
        BloomParameters bloom = parameters.getBloomParameters();
        if (!bloom.isEnabled() || bloom.getBloomIntensity() <= 0.0) {
            log.debug("Bloom disabled");
            return image;
        }

        log.info("Applying bloom: radius={}, intensity={}, threshold={}",
                bloom.getBloomRadius(), bloom.getBloomIntensity(), bloom.getBloomThreshold());

        return BloomPostProcessor.builder()
                .bloomRadius(bloom.getBloomRadius())
                .bloomIntensity(bloom.getBloomIntensity())
                .bloomThreshold(bloom.getBloomThreshold())
                .build()
                .apply(image);
    }
}
