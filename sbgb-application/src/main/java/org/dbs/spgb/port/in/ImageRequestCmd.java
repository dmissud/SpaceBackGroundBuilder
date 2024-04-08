package org.dbs.spgb.port.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class ImageRequestCmd {
    public ImageRequestCmd(@JsonProperty("sizeCmd") SizeCmd sizeCmd, @JsonProperty("colorCmd") ColorCmd colorCmd) {
        this.sizeCmd = sizeCmd;
        this.colorCmd = colorCmd;
    }

    private SizeCmd sizeCmd;
    private ColorCmd colorCmd;

    @Getter
    public static class SizeCmd {
        public SizeCmd(@JsonProperty("height") int height,
                       @JsonProperty("width") int width,
                       @JsonProperty("seed") int seed) {
            this.height = height;
            this.width = width;
            this.seed = seed;
        }

        @Min(100)
        @Max(4000)
        private final int height;
        @Min(100)
        @Max(4000)
        private final int width;
        @Min(1)
        private final int seed;
    }

    @Getter
    public static class ColorCmd {
        public ColorCmd(@JsonProperty("back") String back,
                        @JsonProperty("middle") String middle,
                        @JsonProperty("fore") String fore,
                        @JsonProperty("backThreshold") double backThreshold,
                        @JsonProperty("middleThreshold") double middleThreshold) {
            this.back = back;
            this.middle = middle;
            this.fore = fore;
            this.backThreshold = backThreshold;
            this.middleThreshold = middleThreshold;
        }

        @NotNull
        @Valid
        @Pattern(regexp = "^#([a-fA-F0-9]{6})$")
        private final String back;
        @NotNull
        @Valid
        @Pattern(regexp = "^#([a-fA-F0-9]{6})$")
        private final String middle;
        @NotNull
        @Valid
        @Pattern(regexp = "^#([a-fA-F0-9]{6})$")
        private final String fore;
        @DecimalMin("0.1")
        private final double backThreshold;
        @DecimalMin("0.1")
        private final double middleThreshold;
    }

}
