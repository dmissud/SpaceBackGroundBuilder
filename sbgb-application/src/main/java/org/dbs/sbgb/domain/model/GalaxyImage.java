package org.dbs.sbgb.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GalaxyImage {
    private UUID id;
    private String description;
    private int note;

    private GalaxyStructure galaxyStructure;

    private byte[] image;

    /**
     * Creates a builder for GalaxyImage.
     * Useful for constructing instances with a fluent API.
     */
    public static GalaxyImageBuilder builder() {
        return new GalaxyImageBuilder();
    }

    public static class GalaxyImageBuilder {
        private UUID id;
        private String description;
        private int note;
        private GalaxyStructure galaxyStructure;
        private byte[] image;

        public GalaxyImageBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public GalaxyImageBuilder description(String description) {
            this.description = description;
            return this;
        }

        public GalaxyImageBuilder note(int note) {
            this.note = note;
            return this;
        }

        public GalaxyImageBuilder galaxyStructure(GalaxyStructure galaxyStructure) {
            this.galaxyStructure = galaxyStructure;
            return this;
        }

        public GalaxyImageBuilder image(byte[] image) {
            this.image = image;
            return this;
        }

        public GalaxyImage build() {
            GalaxyImage galaxyImage = new GalaxyImage();
            galaxyImage.setId(this.id);
            galaxyImage.setDescription(this.description);
            galaxyImage.setNote(this.note);
            galaxyImage.setGalaxyStructure(this.galaxyStructure);
            galaxyImage.setImage(this.image);
            return galaxyImage;
        }
    }
}
