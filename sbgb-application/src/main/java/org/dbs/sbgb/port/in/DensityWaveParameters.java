package org.dbs.sbgb.port.in;

public record DensityWaveParameters(
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
    public static DensityWaveParameters defaults() {
        return new DensityWaveParameters(
                15000.0f, 2250.0f, 0.019f, 0.85f, 0.95f, 60000, false, 2, 50.0f, 4000.0f, 3
        );
    }
}
