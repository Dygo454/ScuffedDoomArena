package com.DAConcepts;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BufferedImageLoader {
    private BufferedImage image;

    public BufferedImage LoadImage(String path) throws IOException {
        File imageFile = new File(path);
        image = ImageIO.read(imageFile);
        return image;
    }
}
