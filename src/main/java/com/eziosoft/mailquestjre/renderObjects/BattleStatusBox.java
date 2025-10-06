package com.eziosoft.mailquestjre.renderObjects;

import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderObjects.DrawableObject;
import com.alysoft.dankengine.renderer.DankColor;
import com.alysoft.dankengine.renderer.DankFont;
import com.eziosoft.mailquestjre.entities.BattleEntity;

public class BattleStatusBox implements DrawableObject {

    // variables we need for later
    private int hp;
    private int maxhp;
    private int magic;
    private int maxmagic;
    private int level;
    private String name;
    private String status_cond;
    /*
    drawpos information
    true: draw for player
    false: draw for foe
     */
    private boolean drawpos;

    public BattleStatusBox(BattleEntity ent, boolean drawpos, String status_cond){
        this.level = ent.getLevel();
        this.hp = ent.getHealth();
        this.maxhp = ent.getMaxHealth();
        this.magic = ent.getMagic();
        this.maxmagic = ent.getMaxMagic();
        this.name = ent.getName();
        this.drawpos = drawpos;
        this.status_cond = status_cond;
    }


    @Override
    public void drawObject(GraphicsBackend gfx) {
        // setup the draw positions
        int x;
        int y;
        if (!this.drawpos){
            x = 10;
            y = 10;
        } else {
            x = 300;
            y = 220;
        }
        // set the color for the outer box
        // draw outer rectangle
        gfx.drawRectangleFilled(x, y, 200, 170, DankColor.pink);
        // change color
        // draw inner rectangle
        gfx.drawRectangleFilled(x + 8, y + 8, 184, 154, DankColor.lightGray);
        // change color to black and setup for font rendering
        DankFont thefont = new DankFont("helvetica", 0, 27);
        // draw information required in the status box
        gfx.drawTextSimple(x + 10, y + 30, thefont, DankColor.black, this.name);
        gfx.drawTextSimple(x + 10, y + 60, thefont, DankColor.black, "Lv." + Integer.toString(this.level));
        gfx.drawTextSimple( x + 10, y + 90, thefont, DankColor.black, "HP: " + Integer.toString(this.hp) + "/" + Integer.toString(this.maxhp));
        gfx.drawTextSimple(x + 10, y + 120, thefont, DankColor.black, "MP: " + Integer.toString(this.magic) + "/" + Integer.toString(this.maxmagic));
        gfx.drawTextSimple(x + 10, y + 150, thefont, DankColor.black, this.status_cond);
    }
}
