package com.eziosoft.mailquestjre.renderObjects;

import com.alysoft.dankengine.Main;
import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderObjects.DrawableObject;
import com.alysoft.dankengine.renderer.DankColor;
import com.alysoft.dankengine.renderer.DankGraphic;
import com.alysoft.dankengine.utility.Camera;

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
            throw new RuntimeException("Something went wrong loading a sprite!", e);
        }
    }

    /**
     * for the sliding animation at the start of a battle
     * -the player slides in from the right going left
     * -the foe slides from the left going right
     */
    private int final_x;
    private int final_y;
    public void setupForSlide(boolean is_foe){
        // copy the y and x pos to the final variables
        this.final_x = this.xpos;
        this.final_y = this.ypos;
        // reset them both
        if (is_foe){
            // foe is drawn at x = 300, so move off the screen to the left into negative
            this.xpos = -200;
        } else {
            // player is drawn at x = 0, move to 500
            this.xpos = 500;
        }
    }

    /**
     * used by the battle engine to check if the slide in has finished or not
     * @return true if sliding is down, false if no
     */
    public boolean slide_done(){
        return this.xpos == this.final_x;
    }
    // how many pixels per frame to move the graphics
    private static final int slide_constant = 25;
    public void doSlideFrame(boolean is_foe){
        if (is_foe){
            // foe is sliding from the left, so add to its x
            this.xpos += slide_constant;
            if (this.xpos > this.final_x){
                this.xpos = this.final_x;
            }
        } else {
            // player is sliding from the right; subtract from its x
            this.xpos -= slide_constant;
            if (this.xpos < this.final_x){
                this.xpos = this.final_x;
            }
        }
    }

    @Override
    public void drawObject(GraphicsBackend gfx, Camera camera) {
        if (this.sprite != null) {
            this.sprite.drawGraphic(this.xpos, this.ypos, gfx);
        } else {
            gfx.drawRectangleFilled(this.xpos, this.ypos, 200, 200, DankColor.red);
        }
    }
}
