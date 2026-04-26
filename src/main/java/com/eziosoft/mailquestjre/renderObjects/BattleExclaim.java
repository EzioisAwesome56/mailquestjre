package com.eziosoft.mailquestjre.renderObjects;

import com.alysoft.dankengine.Main;
import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderObjects.DrawableObject;
import com.alysoft.dankengine.renderer.DankColor;
import com.alysoft.dankengine.renderer.DankFont;
import com.alysoft.dankengine.utility.Camera;

public class BattleExclaim implements DrawableObject {
    private int tilex;
    private int tiley;

    public BattleExclaim(int x, int y){
        this.tilex = x;
        this.tiley = y;
    }

    @Override
    public void drawObject(GraphicsBackend gfx, Camera camera) {
        // calcuate the base x and y of where to draw the exclaim box
        int base_x = (Main.tile_size * this.tilex) - camera.getX();
        int base_y = (Main.tile_size * this.tiley) - camera.getY();
        // draw the outline of the box
        gfx.drawRectangle(base_x, base_y, Main.tile_size, Main.tile_size, DankColor.black);
        // change color and draw inner box
        gfx.drawRectangleFilled(base_x + 1, base_y + 1, Main.tile_size - 1, Main.tile_size - 1, DankColor.yellow);
        // change color and set font
        gfx.drawTextSimple(base_x + 15, base_y + 45, new DankFont("helvetica", 0, 50), DankColor.black, "!");
    }
}
