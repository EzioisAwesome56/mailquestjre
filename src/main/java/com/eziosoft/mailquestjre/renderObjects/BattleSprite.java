package com.eziosoft.mailquestjre.renderObjects;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class BattleSprite implements DrawableObject {

    // setup our cached information here
    private BufferedImage sprite;
    private int xpos;
    private int ypos;

    public BattleSprite(String resource, int x, int y){
        // set x and y
        this.xpos = x;
        this.ypos = y;
        // attempt to aquire the sprite from resources
        try {
            InputStream stream = BattleSprite.class.getResourceAsStream(resource);
            this.sprite = ImageIO.read(stream);
        } catch (Exception e){
            System.err.println("Something has gone horribly wrong!");
            e.printStackTrace();
            //System.exit(2);
        }
    }

    @Override
    public void drawObject(Graphics2D gfx) {
        // TODO: replace this with actual code
        if (this.sprite != null) {
            gfx.drawImage(this.sprite, this.xpos, this.ypos, null);
        } else {
            gfx.setColor(Color.red);
            gfx.fillRect(this.xpos, this.ypos, 200, 200);
        }
    }
}
