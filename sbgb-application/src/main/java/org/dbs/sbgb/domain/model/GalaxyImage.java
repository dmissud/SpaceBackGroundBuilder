package org.dbs.sbgb.domain.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class GalaxyImage {
    @Id
    private UUID id;
    private String name;
    private String description;
    private int note;

    @Embedded
    private GalaxyStructure galaxyStructure;

    @Lob
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
        private String name;
        private String description;
        private int note;
        private GalaxyStructure galaxyStructure;
        private byte[] image;

        public GalaxyImageBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public GalaxyImageBuilder name(String name) {
            this.name = name;
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
            galaxyImage.setName(this.name);
            galaxyImage.setDescription(this.description);
            galaxyImage.setNote(this.note);
            galaxyImage.setGalaxyStructure(this.galaxyStructure);
            galaxyImage.setImage(this.image);
            return galaxyImage;
        }
    }
}
