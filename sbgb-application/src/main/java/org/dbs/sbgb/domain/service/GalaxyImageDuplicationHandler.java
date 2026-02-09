package org.dbs.sbgb.domain.service;

import lombok.RequiredArgsConstructor;
import org.dbs.sbgb.domain.exception.ImageNameAlreadyExistsException;
import org.dbs.sbgb.domain.model.GalaxyImage;
import org.dbs.sbgb.port.out.GalaxyImageRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Handles galaxy image name duplication logic.
 * Determines whether to reuse an existing image ID or create a new one.
 */
@Component
@RequiredArgsConstructor
public class GalaxyImageDuplicationHandler {

    private final GalaxyImageRepository galaxyImageRepository;

    /**
     * Resolves the ID to use for a galaxy image, handling name duplication.
     *
     * @param name        the name of the galaxy image
     * @param forceUpdate whether to update if a duplicate name exists
     * @return the UUID to use (existing or new)
     * @throws ImageNameAlreadyExistsException if duplicate exists and forceUpdate is false
     */
    public UUID resolveId(String name, boolean forceUpdate) {
        validateName(name);

        Optional<GalaxyImage> existingImage = galaxyImageRepository.findByName(name);

        if (existingImage.isPresent() && !forceUpdate) {
            throw new ImageNameAlreadyExistsException(name);
        }

        return existingImage
                .map(GalaxyImage::getId)
                .orElse(UUID.randomUUID());
    }

    /**
     * Gets the note value from an existing image, or 0 if not found.
     *
     * @param name the name of the galaxy image
     * @return the note value
     */
    public int resolveNote(String name) {
        return galaxyImageRepository.findByName(name)
                .map(GalaxyImage::getNote)
                .orElse(0);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom de la galaxie est obligatoire");
        }
    }
}
