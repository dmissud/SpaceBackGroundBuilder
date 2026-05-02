package org.dbs.sbgb.domain.model.densitywave;

public record DensityWaveGalaxyParams(
        float galaxyRadius,
        float coreRadius,
        float angleOffset,
        float eccentricityInner,
        float eccentricityOuter,
        int starCount,
        boolean hasDarkMatter,
        int pertN,
        float pertAmp,
        float baseTemp,
        int h2Density
) {
    public static final float DEFAULT_BASE_TEMP = 4000.0f;
    public static final float DEFAULT_ANGLE_OFFSET = 0.019f;
    public static final float DEFAULT_ECCENTRICITY_INNER = 0.85f;
    public static final float DEFAULT_ECCENTRICITY_OUTER = 0.95f;
    public static final int DEFAULT_H2_DENSITY = 3;

    public static DensityWaveGalaxyParams defaultParams(float galaxyRadius, int starCount) {
        return new DensityWaveGalaxyParams(
                galaxyRadius,
                galaxyRadius * 0.15f,
                DEFAULT_ANGLE_OFFSET,
                DEFAULT_ECCENTRICITY_INNER,
                DEFAULT_ECCENTRICITY_OUTER,
                starCount,
                false,
                2,
                50.0f,
                DEFAULT_BASE_TEMP,
                DEFAULT_H2_DENSITY
        );
    }
}
