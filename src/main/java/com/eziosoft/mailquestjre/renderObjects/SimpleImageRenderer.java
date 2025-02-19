package com.eziosoft.mailquestjre.renderObjects;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SimpleImageRenderer implements DrawableObject{
    private final int x;
    private final int y;
    private final BufferedImage content;

    public SimpleImageRenderer(BufferedImage img){
        this(img, 0, 0);
    }
    public SimpleImageRenderer(BufferedImage img, int x, int y){
        this.content = img;
        this.x = x;
        this.y = y;
    }

    @Override
    public void drawObject(Graphics2D gfx) {
        // draw using the image and x/y coords
        gfx.drawImage(this.content, this.x, this.y, null);
    }
}
