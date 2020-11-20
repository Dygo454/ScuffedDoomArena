package com.DAConcepts;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class Texture {
    public static RenderClass E1M1Floor = loadBitmap("res/Textures/doomE1M1FloorText.png");
    public static RenderClass E1M1Roof = loadBitmap("res/Textures/doomE1M1RoofText.png");
    public static RenderClass E1M1Wall = loadBitmap("res/Textures/doomE1M1WallText.png");

    public static RenderClass loadBitmap(String path) {
        try {
            BufferedImageLoader loader = new BufferedImageLoader();
            BufferedImage image = loader.LoadImage(path);;
            int width = image.getWidth();
            int height = image.getHeight();
            RenderClass result = new RenderClass(width,height);
            image.getRGB(0,0,width,height,result.PIXELS,0,width);
            return result;
        } catch (IOException e) {
            System.out.println("Error reading "+path);
            throw new RuntimeException(e);
        }
    }
}
