package org.dbs.sbgb.domain.model.densitywave;

public class DensityWaveOrbitalMechanics {

    private static final double PC_TO_KM = 3.08567758e13;
    private static final double SEC_PER_YEAR = 31_536_000.0;
    private static final double ORBITAL_VELOCITY_KMS = 220.0;

    private final float galaxyRadius;
    private final float coreRadius;
    private final float farFieldRadius;
    private final float eccentricityInner;
    private final float eccentricityOuter;
    private final float angleOffset;

    public DensityWaveOrbitalMechanics(float galaxyRadius, float coreRadius,
                                       float eccentricityInner, float eccentricityOuter,
                                       float angleOffset) {
        this.galaxyRadius = galaxyRadius;
        this.coreRadius = coreRadius;
        this.farFieldRadius = galaxyRadius * 2.0f;
        this.eccentricityInner = eccentricityInner;
        this.eccentricityOuter = eccentricityOuter;
        this.angleOffset = angleOffset;
    }

    public float eccentricity(float radius) {
        if (radius < coreRadius) {
            return 1.0f + (radius / coreRadius) * (eccentricityInner - 1.0f);
        }
        if (radius <= galaxyRadius) {
            float t = (radius - coreRadius) / (galaxyRadius - coreRadius);
            return eccentricityInner + t * (eccentricityOuter - eccentricityInner);
        }
        if (radius < farFieldRadius) {
            float t = (radius - galaxyRadius) / (farFieldRadius - galaxyRadius);
            return eccentricityOuter + t * (1.0f - eccentricityOuter);
        }
        return 1.0f;
    }

    public float angularOffset(float radius) {
        return radius * angleOffset;
    }

    public double orbitalVelocity(float radius) {
        if (radius <= 0) return 0.0;
        double circumferenceKm = 2.0 * Math.PI * radius * PC_TO_KM;
        double periodYears = circumferenceKm / ORBITAL_VELOCITY_KMS / SEC_PER_YEAR;
        return 360.0 / periodYears;
    }
}
