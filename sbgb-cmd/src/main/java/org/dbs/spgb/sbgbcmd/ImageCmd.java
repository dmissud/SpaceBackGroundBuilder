package org.dbs.spgb.sbgbcmd;

import org.dbs.spgb.domain.model.SpaceBackGround;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@ShellComponent
public class ImageCmd {

    @ShellMethod("Generate Spring Boot Shell command")
    public void buildImage(long seed, String fileName) {
        SpaceBackGround spaceBackGround = new SpaceBackGround.Builder()
                .build();

        BufferedImage image = spaceBackGround.create(seed);

        // Add .png extension if not present
        if (!fileName.endsWith(".png")) {
            fileName += ".png";
        }

        // Save the image
        File outputfile = new File(fileName);

        try {
            ImageIO.write(image, "png", outputfile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}