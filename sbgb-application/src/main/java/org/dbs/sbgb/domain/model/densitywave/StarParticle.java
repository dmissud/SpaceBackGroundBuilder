package org.dbs.sbgb.domain.model.densitywave;

public record StarParticle(
        float theta0,
        float velTheta,
        float tiltAngle,
        float semiMajorAxis,
        float semiMinorAxis,
        float temperature,
        float magnitude,
        StarParticleType type
) {}
