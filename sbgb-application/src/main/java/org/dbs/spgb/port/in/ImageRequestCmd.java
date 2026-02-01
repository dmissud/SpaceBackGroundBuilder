package org.dbs.spgb.port.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequestCmd {
    private String name;
    private String type;
    private SizeCmd sizeCmd;
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
    }
}