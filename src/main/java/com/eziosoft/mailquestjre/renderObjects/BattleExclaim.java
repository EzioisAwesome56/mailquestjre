package com.eziosoft.mailquestjre.renderObjects;

import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderObjects.DrawableObject;
import com.alysoft.dankengine.renderer.DankColor;
import com.alysoft.dankengine.renderer.DankFont;

public class BattleExclaim implements DrawableObject {
    private int tilex;
    private int tiley;

    public BattleExclaim(int x, int y){
        this.tilex = x;
        this.tiley = y;
    }

    @Override
    public void drawObject(GraphicsBackend gfx) {
        // draw the outline of the box
        gfx.drawRectangle(25 * this.tilex, 25 * this.tiley, 25, 25, DankColor.black);
        // change color and draw inner box
        gfx.drawRectangleFilled((25 * this.tilex) + 1, (25 * this.tiley) + 1, 24, 24, DankColor.yellow);
        // change color and set font
        gfx.drawTextSimple((25 * this.tilex) + 9, (25 * this.tiley) + 20, new DankFont("helvetica", 0, 23), DankColor.black, "!");
    }
}
