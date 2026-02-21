package org.dbs.sbgb.port.in;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record ColorParameters(
        @Pattern(regexp = "NEBULA|CLASSIC|WARM|COLD|INFRARED|EMERALD|CUSTOM", message = "doit correspondre à \"NEBULA|CLASSIC|WARM|COLD|INFRARED|EMERALD|CUSTOM\"") String colorPalette,
        @Pattern(regexp = "^#([a-fA-F0-9]{6})$") String spaceBackgroundColor,
        @Pattern(regexp = "^#([a-fA-F0-9]{6})$") String coreColor,
        @Pattern(regexp = "^#([a-fA-F0-9]{6})$") String armColor,
        @Pattern(regexp = "^#([a-fA-F0-9]{6})$") String outerColor) {
    public static ColorParameters classicPalette() {
        return ColorParameters.builder()
                .colorPalette("CLASSIC")
                .spaceBackgroundColor("#050510")
                .coreColor("#FFFADC")
                .armColor("#B4C8FF")
                .outerColor("#3C5078")
                .build();
    }
}
