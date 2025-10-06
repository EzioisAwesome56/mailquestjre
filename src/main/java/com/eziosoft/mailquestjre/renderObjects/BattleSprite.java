package com.eziosoft.mailquestjre.renderObjects;

import com.alysoft.dankengine.Main;
import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderObjects.DrawableObject;
import com.alysoft.dankengine.renderer.DankColor;
import com.alysoft.dankengine.renderer.DankGraphic;

public class BattleSprite implements DrawableObject {

    // setup our cached information here
    private DankGraphic sprite;
    private int xpos;
    private int ypos;

    public BattleSprite(String resource, int x, int y){
        // set x and y
        this.xpos = x;
        this.ypos = y;
        // attempt to aquire the sprite from resources
        try {
            this.sprite = Main.getFunctionalBackend().getEngineGraphicResource(resource);
        } catch (Exception e){
            System.err.println("Something has gone horribly wrong!");
            e.printStackTrace();
            //System.exit(2);
        }
    }

    @Override
    public void drawObject(GraphicsBackend gfx) {
        if (this.sprite != null) {
            this.sprite.drawGraphic(this.xpos, this.ypos, gfx);
        } else {
            gfx.drawRectangleFilled(this.xpos, this.ypos, 200, 200, DankColor.red);
        }
    }
}
