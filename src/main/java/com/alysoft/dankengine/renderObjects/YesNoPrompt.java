package com.alysoft.dankengine.renderObjects;

import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderer.DankColor;
import com.alysoft.dankengine.renderer.DankFont;
import com.alysoft.dankengine.utility.Camera;

public class YesNoPrompt implements DrawableObject {

    private final int itemselected;

    public YesNoPrompt(int item){
        this.itemselected = item;
    }


    @Override
    public void drawObject(GraphicsBackend gfx, Camera camera) {
        // set outer color
        // draw rectangle
        gfx.drawRectangleFilled(400, 200, 100, 90, DankColor.pink);
        // change color
        // draw inner rectangle
        gfx.drawRectangleFilled(408, 208, 84, 74, DankColor.lightGray);
        // setup the font rendering
        DankFont thefont = new DankFont("helvetica", 0, 27);
        // draw the 0th menu item
        gfx.drawTextSimple(430, 235, thefont, DankColor.black, "Yes");
        // etc draw all the others
        gfx.drawTextSimple(430, 265, thefont, DankColor.black, "No");
        // next: draw arrow
        // we need to calculate how far down to move it
        int addy = 30 * this.itemselected;
        gfx.drawPolygonFilled(new int[]{410, 410, 425}, new int[]{210 + addy, 240 + addy, 224 + addy}, 3, DankColor.green);
    }
}
