package org.dbs.sbgb.port.in;

public record StarParticleDto(
        float theta0,
        float velTheta,
        float tiltAngle,
        float semiMajorAxis,
        float semiMinorAxis,
        float temperature,
        float magnitude,
        String type
) {}
