package org.dbs.spgb.port.in;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;

import static org.dbs.spgb.common.validation.Validation.validate;

public interface BuildNoiseImageUseCase {
    byte[] buildNoiseImage(ImageRequestCmd imageRequestCmd) throws IOException;

    @Getter
    @Setter
    @NoArgsConstructor
    class ImageRequestCmd {
        @Min(100)
        @Max(4000)
        private int height;
        @Min(100)
        @Max(4000)
        private int width;
        @Min(1)
        private int seed;

        public ImageRequestCmd(int height, int width, int seed) {
            this.height = height;
            this.width = width;
            this.seed = seed;
            validate(this);
        }
    }
}
