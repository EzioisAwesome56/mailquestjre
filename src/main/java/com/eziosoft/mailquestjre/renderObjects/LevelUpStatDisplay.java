package com.eziosoft.mailquestjre.renderObjects;

import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderObjects.DrawableObject;
import com.alysoft.dankengine.renderer.DankColor;
import com.alysoft.dankengine.renderer.DankFont;

public class LevelUpStatDisplay implements DrawableObject {
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
    public void drawObject(GraphicsBackend gfx) {
        // set color to pink
        // draw the outer box
        gfx.drawRectangleFilled(4, 4, 300, 200, DankColor.pink);
        // switch color and draw inner box
        gfx.drawRectangleFilled(12, 12, 284, 184, DankColor.lightGray);
        // set font and change color
        DankFont thefont = new DankFont("helvetica", 0, 27);
        // draw all the states
        gfx.drawTextSimple(14, 34, thefont, DankColor.black, "HP: " + this.hp + " + " + this.hp_add);
        gfx.drawTextSimple(14, 64, thefont, DankColor.black, "ATK: " + this.atk + " + " + this.atk_add);
        gfx.drawTextSimple(14, 94, thefont, DankColor.black, "DEF: " + this.def + " + " + this.def_add);
        gfx.drawTextSimple(14, 124, thefont, DankColor.black, "MAG: " + this.magic + " + " + this.magic_add);
        // give player tip about picking stat to boost
        gfx.drawTextSimple(14, 154, thefont, DankColor.black, "Pick stat to give");
        gfx.drawTextSimple(14, 184, thefont, DankColor.black, "a boost to!");
        // draw an arrow
        gfx.drawPolygonFilled(new int[]{280, 280, 260}, new int[]{14 + (this.selected_item * 30), 34 + (this.selected_item * 30), 24 + (this.selected_item * 30)}, 3, DankColor.green);
    }
}
