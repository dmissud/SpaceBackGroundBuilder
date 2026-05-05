package org.dbs.sbgb.domain.model.particle;

import org.dbs.sbgb.domain.model.parameters.EllipticalShapeParameters;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EllipticalParticleGeneratorTest {

    private static final float GALAXY_RADIUS = 15000f;
    private static final int STAR_COUNT = 2000;
    private static final long SEED = 42L;

    private EllipticalParticleGenerator generator(EllipticalShapeParameters shape) {
        return new EllipticalParticleGenerator(GALAXY_RADIUS, STAR_COUNT, shape, SEED);
    }

    @Test
    void generates_expected_number_of_points() {
        List<StellarPoint> points = generator(defaultShape()).generate();
        assertThat(points).hasSize(STAR_COUNT);
    }

    @Test
    void all_points_within_galaxy_radius() {
        List<StellarPoint> points = generator(defaultShape()).generate();
        for (StellarPoint p : points) {
            double r = Math.sqrt(p.x() * p.x() + p.y() * p.y());
            assertThat(r).isLessThanOrEqualTo(GALAXY_RADIUS * 3);
        }
    }

    @Test
    void temperature_higher_at_center_than_periphery() {
        List<StellarPoint> points = generator(defaultShape()).generate();
        double innerTemp = points.stream()
                .filter(p -> Math.sqrt(p.x() * p.x() + p.y() * p.y()) < GALAXY_RADIUS * 0.1)
                .mapToDouble(StellarPoint::temperature)
                .average()
                .orElseThrow();
        double outerTemp = points.stream()
                .filter(p -> Math.sqrt(p.x() * p.x() + p.y() * p.y()) > GALAXY_RADIUS * 0.5)
                .mapToDouble(StellarPoint::temperature)
                .average()
                .orElseThrow();
        assertThat(innerTemp).isGreaterThan(outerTemp);
    }

    @Test
    void magnitude_is_positive_for_all_points() {
        List<StellarPoint> points = generator(defaultShape()).generate();
        points.forEach(p -> assertThat(p.magnitude()).isGreaterThan(0.0f));
    }

    @Test
    void flattening_produces_oblate_distribution() {
        EllipticalShapeParameters flat = EllipticalShapeParameters.builder().axisRatio(0.3).build();
        List<StellarPoint> points = generator(flat).generate();
        double maxX = points.stream().mapToDouble(p -> Math.abs(p.x())).max().orElseThrow();
        double maxY = points.stream().mapToDouble(p -> Math.abs(p.y())).max().orElseThrow();
        assertThat(maxY).isLessThan(maxX);
    }

    @Test
    void circular_galaxy_has_symmetric_extent() {
        EllipticalShapeParameters circular = EllipticalShapeParameters.builder().axisRatio(1.0).build();
        List<StellarPoint> points = generator(circular).generate();
        double maxX = points.stream().mapToDouble(p -> Math.abs(p.x())).max().orElseThrow();
        double maxY = points.stream().mapToDouble(p -> Math.abs(p.y())).max().orElseThrow();
        double ratio = Math.min(maxX, maxY) / Math.max(maxX, maxY);
        assertThat(ratio).isGreaterThan(0.7);
    }

    private EllipticalShapeParameters defaultShape() {
        return EllipticalShapeParameters.builder().build();
    }
}
