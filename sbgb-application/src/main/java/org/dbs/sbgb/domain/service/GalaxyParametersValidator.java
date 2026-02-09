package org.dbs.sbgb.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.dbs.sbgb.domain.model.GalaxyParameters;
import org.dbs.sbgb.domain.model.GalaxyType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates coherence of galaxy parameters according to galaxy type
 */
@Component
@Slf4j
public class GalaxyParametersValidator {

    public void validate(GalaxyParameters parameters) {
        List<String> errors = new ArrayList<>();

        validateCommonParameters(parameters, errors);
        validateTypeSpecificParameters(parameters, errors);

        if (!errors.isEmpty()) {
            String errorMessage = String.join(", ", errors);
            log.error("Galaxy parameters validation failed: {}", errorMessage);
            throw new IllegalArgumentException("Invalid galaxy parameters: " + errorMessage);
        }
    }

    private void validateCommonParameters(GalaxyParameters parameters, List<String> errors) {
        if (parameters.getCoreParameters() == null) {
            errors.add("coreParameters must not be null");
            return;
        }

        if (parameters.getCoreParameters().getCoreSize() <= 0 || parameters.getCoreParameters().getCoreSize() > 1) {
            errors.add("coreSize must be between 0 and 1");
        }

        if (parameters.getCoreParameters().getGalaxyRadius() <= 0) {
            errors.add("galaxyRadius must be positive");
        }

        if (parameters.getNoiseTextureParameters() == null) {
            errors.add("noiseTextureParameters must not be null");
            return;
        }

        if (parameters.getNoiseTextureParameters().getOctaves() < 1 || parameters.getNoiseTextureParameters().getOctaves() > 10) {
            errors.add("octaves must be between 1 and 10");
        }

        if (parameters.getNoiseTextureParameters().getPersistence() < 0 || parameters.getNoiseTextureParameters().getPersistence() > 1) {
            errors.add("persistence must be between 0 and 1");
        }

        if (parameters.getNoiseTextureParameters().getLacunarity() < 1) {
            errors.add("lacunarity must be >= 1");
        }

        if (parameters.getNoiseTextureParameters().getScale() <= 0) {
            errors.add("scale must be positive");
        }
    }

    private void validateTypeSpecificParameters(GalaxyParameters parameters, List<String> errors) {
        GalaxyType type = parameters.getGalaxyType();

        switch (type) {
            case SPIRAL -> validateSpiralParameters(parameters, errors);
            case VORONOI_CLUSTER -> validateVoronoiParameters(parameters, errors);
            case ELLIPTICAL -> validateEllipticalParameters(parameters, errors);
            case RING -> validateRingParameters(parameters, errors);
            case IRREGULAR -> validateIrregularParameters(parameters, errors);
        }
    }

    private void validateSpiralParameters(GalaxyParameters parameters, List<String> errors) {
        if (parameters.getSpiralParameters() == null) {
            errors.add("spiralParameters must not be null for SPIRAL type");
            return;
        }

        if (parameters.getSpiralParameters().getNumberOfArms() < 1 || parameters.getSpiralParameters().getNumberOfArms() > 10) {
            errors.add("numberOfArms must be between 1 and 10");
        }

        if (parameters.getSpiralParameters().getArmWidth() <= 0) {
            errors.add("armWidth must be positive");
        }

        if (parameters.getSpiralParameters().getArmRotation() < 0) {
            errors.add("armRotation must be >= 0");
        }
    }

    private void validateVoronoiParameters(GalaxyParameters parameters, List<String> errors) {
        if (parameters.getVoronoiParameters() == null) {
            errors.add("voronoiParameters must not be null for VORONOI_CLUSTER type");
            return;
        }

        if (parameters.getVoronoiParameters().getClusterCount() < 1 || parameters.getVoronoiParameters().getClusterCount() > 100) {
            errors.add("clusterCount must be between 1 and 100");
        }

        if (parameters.getVoronoiParameters().getClusterSize() <= 0) {
            errors.add("clusterSize must be positive");
        }

        if (parameters.getVoronoiParameters().getClusterConcentration() < 0 || parameters.getVoronoiParameters().getClusterConcentration() > 10) {
            errors.add("clusterConcentration must be between 0 and 10");
        }
    }

    private void validateEllipticalParameters(GalaxyParameters parameters, List<String> errors) {
        if (parameters.getEllipticalParameters() == null) {
            errors.add("ellipticalParameters must not be null for ELLIPTICAL type");
            return;
        }

        if (parameters.getEllipticalParameters().getSersicIndex() < 0.5 || parameters.getEllipticalParameters().getSersicIndex() > 10) {
            errors.add("sersicIndex must be between 0.5 and 10");
        }

        if (parameters.getEllipticalParameters().getAxisRatio() < 0.1 || parameters.getEllipticalParameters().getAxisRatio() > 1) {
            errors.add("axisRatio must be between 0.1 and 1");
        }

        if (parameters.getEllipticalParameters().getOrientationAngle() < 0 || parameters.getEllipticalParameters().getOrientationAngle() > 360) {
            errors.add("orientationAngle must be between 0 and 360");
        }
    }

    private void validateRingParameters(GalaxyParameters parameters, List<String> errors) {
        if (parameters.getRingParameters() == null) {
            errors.add("ringParameters must not be null for RING type");
            return;
        }

        if (parameters.getRingParameters().getRingRadius() <= 0) {
            errors.add("ringRadius must be positive");
        }

        if (parameters.getRingParameters().getRingWidth() <= 0) {
            errors.add("ringWidth must be positive");
        }

        if (parameters.getRingParameters().getRingIntensity() < 0 || parameters.getRingParameters().getRingIntensity() > 2) {
            errors.add("ringIntensity must be between 0 and 2");
        }

        if (parameters.getRingParameters().getCoreToRingRatio() < 0 || parameters.getRingParameters().getCoreToRingRatio() > 1) {
            errors.add("coreToRingRatio must be between 0 and 1");
        }

        if (parameters.getRingParameters().getRingRadius() - parameters.getRingParameters().getRingWidth() / 2 <= 0) {
            errors.add("ringWidth is too large for the given ringRadius");
        }
    }

    private void validateIrregularParameters(GalaxyParameters parameters, List<String> errors) {
        if (parameters.getIrregularParameters() == null) {
            errors.add("irregularParameters must not be null for IRREGULAR type");
            return;
        }

        if (parameters.getIrregularParameters().getIrregularity() < 0 || parameters.getIrregularParameters().getIrregularity() > 1) {
            errors.add("irregularity must be between 0 and 1");
        }

        if (parameters.getIrregularParameters().getClumpCount() < 1 || parameters.getIrregularParameters().getClumpCount() > 50) {
            errors.add("clumpCount must be between 1 and 50");
        }

        if (parameters.getIrregularParameters().getClumpSize() <= 0) {
            errors.add("clumpSize must be positive");
        }
    }
}
