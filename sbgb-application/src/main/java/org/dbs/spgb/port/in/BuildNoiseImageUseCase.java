package org.dbs.spgb.port.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
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
            private Color back;
            @NotNull
            @Valid
            private Color middle;
            @NotNull
            @Valid
            private Color front;
            @DecimalMin("0.1")
            private double backTreshold;
            @DecimalMin("0.1")
            private double middleTreshold;
        }
    }
}
