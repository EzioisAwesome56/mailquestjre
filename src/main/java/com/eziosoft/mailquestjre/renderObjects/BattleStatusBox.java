package com.eziosoft.mailquestjre.renderObjects;

import com.eziosoft.mailquestjre.entities.BattleEntity;

import java.awt.*;

public class BattleStatusBox implements DrawableObject{

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
    public void drawObject(Graphics2D gfx) {
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
        gfx.setColor(Color.pink);
        // draw outer rectangle
        gfx.fillRect(x, y, 200, 170);
        // change color
        gfx.setColor(Color.lightGray);
        // draw inner rectangle
        gfx.fillRect(x + 8, y + 8, 184, 154);
        // change color to black and setup for font rendering
        gfx.setColor(Color.BLACK);
        gfx.setFont(new Font("helvetica", Font.PLAIN, 27));
        // draw information required in the status box
        gfx.drawString(this.name, x + 10, y + 30);
        gfx.drawString("Lv." + Integer.toString(this.level),  x + 10, y + 60);
        gfx.drawString("HP: " + Integer.toString(this.hp) + "/" + Integer.toString(this.maxhp), x + 10, y + 90);
        gfx.drawString("MP: " + Integer.toString(this.magic) + "/" + Integer.toString(this.maxmagic), x + 10, y + 120);
        gfx.drawString(this.status_cond, x + 10, y + 150);
    }
}
