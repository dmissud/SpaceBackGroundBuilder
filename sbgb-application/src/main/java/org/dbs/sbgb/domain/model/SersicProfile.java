package org.dbs.sbgb.domain.model;

/**
 * Value Object representing the Sérsic luminosity profile.
 * <p>
 * Computes I(r) = Ie · exp(−bn · ((r/re)^(1/n) − 1))
 * where Ie is normalized to 1 at the effective radius re.
 * <p>
 * bn is derived from the Ciotti (1991) / Graham et al. (2005) approximation:
 * bn ≈ 2n − 1/3 + 4/(405n) + 46/(25515n²)
 */
public final class SersicProfile {

    private final double sersicIndex;
    private final double effectiveRadius;
    private final double bn;

    public SersicProfile(double sersicIndex, double effectiveRadius) {
        if (sersicIndex <= 0.0) {
            throw new IllegalArgumentException("sersicIndex must be strictly positive, got: " + sersicIndex);
        }
        if (effectiveRadius <= 0.0) {
            throw new IllegalArgumentException("effectiveRadius must be strictly positive, got: " + effectiveRadius);
        }
        this.sersicIndex = sersicIndex;
        this.effectiveRadius = effectiveRadius;
        this.bn = computeBn(sersicIndex);
    }

    /**
     * Computes the Sérsic intensity at radial distance r.
     * Returns Ie=1 at r=re, higher inside, lower outside.
     */
    public double computeIntensity(double r) {
        double rRatio = r / effectiveRadius;
        return Math.exp(-bn * (Math.pow(rRatio, 1.0 / sersicIndex) - 1.0));
    }

    public double getBn() {
        return bn;
    }

    private static double computeBn(double n) {
        return 2.0 * n - (1.0 / 3.0) + (4.0 / (405.0 * n)) + (46.0 / (25515.0 * n * n));
    }
}
