package org.dbs.sbgb.domain.model.particle;

public class DensityGrid {

    private final int width;
    private final int height;
    private final float[][] cells;

    public DensityGrid(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new float[width][height];
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public float get(int x, int y) {
        if (isOutside(x, y)) return 0.0f;
        return cells[x][y];
    }

    public void add(int x, int y, float intensity) {
        if (isOutside(x, y)) return;
        cells[x][y] += intensity;
    }

    public void set(int x, int y, float value) {
        if (isOutside(x, y)) return;
        cells[x][y] = value;
    }

    public float max() {
        float max = 0.0f;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (cells[x][y] > max) max = cells[x][y];
            }
        }
        return max;
    }

    public float[][] raw() {
        return cells;
    }

    private boolean isOutside(int x, int y) {
        return x < 0 || x >= width || y < 0 || y >= height;
    }
}
