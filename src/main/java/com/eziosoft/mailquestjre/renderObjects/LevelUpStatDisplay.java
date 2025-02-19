package com.eziosoft.mailquestjre.renderObjects;

import java.awt.*;

public class LevelUpStatDisplay implements DrawableObject{
    private final int hp;
    private final int atk;
    private final int def;
    private final int magic;
    private final int hp_add;
    private final int atk_add;
    private final int def_add;
    private final int magic_add;
    // thing for the menu selection
    private final int selected_item;

    public LevelUpStatDisplay(int hp, int hp_add, int atk, int atk_add,
                              int def, int def_add, int magic, int magic_add, int item){
        this.hp = hp;
        this.hp_add = hp_add;
        this.atk = atk;
        this.atk_add = atk_add;
        this.def = def;
        this.def_add = def_add;
        this.magic = magic;
        this.magic_add = magic_add;
        this.selected_item = item;
    }


    @Override
    public void drawObject(Graphics2D gfx) {
        // set color to pink
        gfx.setColor(Color.pink);
        // draw the outer box
        gfx.fillRect(4, 4, 300, 200);
        // switch color and draw inner box
        gfx.setColor(Color.lightGray);
        gfx.fillRect(12, 12, 284, 184);
        // set font and change color
        gfx.setColor(Color.black);
        gfx.setFont(new Font("helvetica", Font.PLAIN, 27));
        // draw all the states
        gfx.drawString("HP: " + this.hp + " + " + this.hp_add, 14, 34);
        gfx.drawString("ATK: " + this.atk + " + " + this.atk_add, 14, 64);
        gfx.drawString("DEF: " + this.def + " + " + this.def_add, 14, 94);
        gfx.drawString("MAG: " + this.magic + " + " + this.magic_add, 14, 124);
        // give player tip about picking stat to boost
        gfx.drawString("Pick stat to give", 14, 154);
        gfx.drawString("a boost to!", 14, 184);
        // draw an arrow
        gfx.setColor(Color.green);
        gfx.fillPolygon(new int[]{280, 280, 260}, new int[]{14 + (this.selected_item * 30), 34 + (this.selected_item * 30), 24 + (this.selected_item * 30)}, 3);
    }
}
