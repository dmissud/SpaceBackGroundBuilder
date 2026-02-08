package org.dbs.spgb.domain.model;

import lombok.Value;

import java.awt.*;

/**
 * Represents a color stop in a gradient
 * Each stop has a position (0.0-1.0) and a color
 */
@Value
public class ColorStop {
    double position;
    Color color;

    public ColorStop(double position, Color color) {
        if (position < 0.0 || position > 1.0) {
            throw new IllegalArgumentException("Position must be between 0.0 and 1.0, got: " + position);
        }
        this.position = position;
        this.color = color;
    }

    public ColorStop(double position, int r, int g, int b) {
        this(position, new Color(r, g, b));
    }
}
