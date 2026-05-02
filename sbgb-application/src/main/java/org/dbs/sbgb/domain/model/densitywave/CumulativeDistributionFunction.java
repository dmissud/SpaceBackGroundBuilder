package org.dbs.sbgb.domain.model.densitywave;

import java.util.ArrayList;
import java.util.List;

public class CumulativeDistributionFunction {

    private double fMin;
    private double fMax;
    private double i0;
    private double k;
    private double a;
    private double rBulge;

    private final List<Double> x1 = new ArrayList<>();
    private final List<Double> y1 = new ArrayList<>();
    private final List<Double> m1 = new ArrayList<>();

    private final List<Double> x2 = new ArrayList<>();
    private final List<Double> y2 = new ArrayList<>();
    private final List<Double> m2 = new ArrayList<>();

    public void setupRealistic(double i0, double k, double a, double rBulge,
                               double min, double max, int steps) {
        this.i0 = i0;
        this.k = k;
        this.a = a;
        this.rBulge = rBulge;
        this.fMin = min;
        this.fMax = max;
        buildCdf(steps);
    }

    public double valFromProb(double prob) {
        int idx = findIndex(x2, prob);
        if (idx >= m2.size()) return fMax;
        double val = y2.get(idx) + m2.get(idx) * (prob - x2.get(idx));
        return Math.max(fMin, val);
    }

    public double probFromVal(double val) {
        int idx = findIndex(x1, val);
        if (idx >= m1.size()) return 1.0;
        return y1.get(idx) + m1.get(idx) * (val - x1.get(idx));
    }

    private void buildCdf(int steps) {
        buildForwardTable(steps);
        normalizeForwardTable();
        computeSlopes(x1, y1, m1);
        buildInverseTable(steps);
        computeSlopes(x2, y2, m2);
    }

    private void buildForwardTable(int steps) {
        double h = (fMax - fMin) / (steps - 1);
        x1.clear();
        y1.clear();
        x1.add(fMin);
        y1.add(0.0);

        double cumulative = 0.0;
        for (int i = 0; i < steps - 2; i += 2) {
            double x0 = fMin + i * h;
            double x1v = fMin + (i + 1) * h;
            double x2v = fMin + (i + 2) * h;
            cumulative += (h / 3.0) * (intensity(x0) + 4.0 * intensity(x1v) + intensity(x2v));
            x1.add(x2v);
            y1.add(cumulative);
        }
    }

    private void normalizeForwardTable() {
        double total = y1.get(y1.size() - 1);
        if (total == 0.0) return;
        for (int i = 0; i < y1.size(); i++) {
            y1.set(i, y1.get(i) / total);
        }
    }

    private void buildInverseTable(int steps) {
        x2.clear();
        y2.clear();
        for (int i = 0; i <= steps; i++) {
            double targetProb = (double) i / steps;
            int idx = findIndex(y1, targetProb);
            x2.add(targetProb);
            if (idx >= x1.size() - 1) {
                y2.add(fMax);
            } else {
                double p0 = y1.get(idx);
                double p1 = y1.get(idx + 1);
                double r0 = x1.get(idx);
                double r1 = x1.get(idx + 1);
                double dp = p1 - p0;
                double interpolated = dp == 0 ? r0 : r0 + (r1 - r0) * (targetProb - p0) / dp;
                y2.add(interpolated);
            }
        }
    }

    private static void computeSlopes(List<Double> xs, List<Double> ys, List<Double> slopes) {
        slopes.clear();
        for (int i = 0; i < xs.size() - 1; i++) {
            double dx = xs.get(i + 1) - xs.get(i);
            double dy = ys.get(i + 1) - ys.get(i);
            slopes.add(dx == 0 ? 0.0 : dy / dx);
        }
        if (!slopes.isEmpty()) {
            slopes.add(slopes.get(slopes.size() - 1));
        }
    }

    private double intensity(double r) {
        if (r < rBulge) {
            return i0 * Math.exp(-k * Math.pow(r, 0.25));
        }
        return i0 * Math.exp(-r / a);
    }

    private static int findIndex(List<Double> values, double target) {
        int low = 0;
        int high = values.size() - 1;
        while (low < high) {
            int mid = (low + high) / 2;
            if (values.get(mid) < target) low = mid + 1;
            else high = mid;
        }
        return low;
    }
}
