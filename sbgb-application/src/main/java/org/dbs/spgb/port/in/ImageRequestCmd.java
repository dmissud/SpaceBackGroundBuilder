package org.dbs.spgb.port.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequestCmd {
    private String name;
    private String description;
    private String type;
    private boolean forceUpdate;
    @Valid
    @NotNull
    private SizeCmd sizeCmd;
    @Valid
    @NotNull
    private ColorCmd colorCmd;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SizeCmd {
        @Min(100)
        @Max(4000)
        private int height;
        @Min(100)
        @Max(4000)
        private int width;
        @Min(1)
        private int seed;
        @Min(1)
        @Max(10)
        @Builder.Default
        private int octaves = 1;
        @DecimalMin("0.0")
        @DecimalMax("1.0")
        @Builder.Default
        private double persistence = 0.5;
        @DecimalMin("1.0")
        @Builder.Default
        private double lacunarity = 2.0;
        @DecimalMin("1.0")
        @DecimalMax("1000.0")
        @Builder.Default
        private double scale = 100.0;
        @Builder.Default
        private String preset = "CUSTOM";
        @Builder.Default
        private boolean useMultiLayer = false;
        @Builder.Default
        private String noiseType = "FBM";
        private List<LayerCmd> layers;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LayerCmd {
        private String name;
        private boolean enabled;
        @Min(1)
        @Max(10)
        private int octaves;
        @DecimalMin("0.0")
        @DecimalMax("1.0")
        private double persistence;
        @DecimalMin("1.0")
        private double lacunarity;
        @DecimalMin("1.0")
        @DecimalMax("1000.0")
        private double scale;
        @DecimalMin("0.0")
        @DecimalMax("1.0")
        private double opacity;
        private String blendMode;
        @Builder.Default
        private String noiseType = "FBM";
        private long seedOffset;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColorCmd {
        @NotNull
        @Valid
        @Pattern(regexp = "^#([a-fA-F0-9]{6})$")
        private String back;
        @NotNull
        @Valid
        @Pattern(regexp = "^#([a-fA-F0-9]{6})$")
        private String middle;
        @NotNull
        @Valid
        @Pattern(regexp = "^#([a-fA-F0-9]{6})$")
        private String fore;
        @DecimalMin("0.1")
        private double backThreshold;
        @DecimalMin("0.1")
        private double middleThreshold;
        @NotNull
        @Builder.Default
        private String interpolationType = "LINEAR";
        @Builder.Default
        private boolean transparentBackground = false;
    }
}