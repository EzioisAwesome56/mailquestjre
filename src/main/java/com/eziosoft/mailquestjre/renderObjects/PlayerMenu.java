package com.eziosoft.mailquestjre.renderObjects;

import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderObjects.DrawableObject;
import com.alysoft.dankengine.renderer.DankColor;
import com.alysoft.dankengine.renderer.DankFont;

public class PlayerMenu implements DrawableObject {

    private final int itemselected;

    public PlayerMenu(int item){
        this.itemselected = item;
    }


    @Override
    public void drawObject(GraphicsBackend gfx) {
        // set outer color
        // draw rectangle
        gfx.drawRectangleFilled(300, 0, 200, 190, DankColor.pink);
        // change color
        // draw inner rectangle
        gfx.drawRectangleFilled(308, 8, 184, 174, DankColor.lightGray);
        // setup the font rendering
        DankFont thefont = new DankFont("helvetica", 0, 27);
        // draw the 0th menu item
        gfx.drawTextSimple(330, 35, thefont, DankColor.black, "Your Stats");
        // etc draw all the others
        gfx.drawTextSimple(330, 65, thefont, DankColor.black, "Items");
        gfx.drawTextSimple(330, 95, thefont, DankColor.black, "Map");
        gfx.drawTextSimple(330, 125, thefont, DankColor.black, "Save");
        gfx.drawTextSimple(330, 155, thefont, DankColor.black, "Quit");
        // TODO: if anything else needs drawn, do so
        // next: draw arrow
        // we need to calculate how far down to move it
        int addy = 30 * this.itemselected;
        gfx.drawPolygonFilled(new int[]{310, 310, 325}, new int[]{10 + addy, 40 + addy, 24 + addy}, 3, DankColor.green);
    }
}
