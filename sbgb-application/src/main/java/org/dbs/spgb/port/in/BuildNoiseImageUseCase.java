package org.dbs.spgb.port.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;

public interface BuildNoiseImageUseCase {
    byte[] buildNoiseImage(ImageRequestCmd imageRequestCmd) throws IOException;

    @Getter
    @Setter
    @NoArgsConstructor
    class ImageRequestCmd {
        private SizeCmd sizeCmd;
        private ColorCmd colorCmd;

        @Getter
        @Setter
        @NoArgsConstructor
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
        @Setter
        @NoArgsConstructor
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
}