package org.dbs.spgb.domain.model;

public enum BlendMode {
    NORMAL,      // Simple alpha blending
    MULTIPLY,    // Darkens (value1 * value2)
    SCREEN,      // Lightens (1 - (1-v1)*(1-v2))
    OVERLAY,     // Dynamic contrast
    ADD          // Additive blending
}
