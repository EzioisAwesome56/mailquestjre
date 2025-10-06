package com.eziosoft.mailquestjre.renderObjects;

import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderObjects.DrawableObject;
import com.alysoft.dankengine.renderer.DankColor;
import com.alysoft.dankengine.renderer.DankFont;

public class StatBoostWheel implements DrawableObject {
    private final int number;

    public StatBoostWheel(int num){
        this.number = num;
    }


    @Override
    public void drawObject(GraphicsBackend gfx) {
        // set color and draw outer box
        gfx.drawRectangleFilled(400, 4, 60, 70, DankColor.pink);
        // draw inner box
        gfx.drawRectangleFilled(408, 12, 44, 54, DankColor.lightGray);
        // setup font and draw number
        gfx.setupDrawString(new DankFont("helvetica", 0, 50), DankColor.black);
        gfx.drawString(Integer.toString(this.number), 412, 54);
        // give the player a hint
        gfx.setupDrawString(new DankFont("helvetica", 0, 13), DankColor.black);
        gfx.drawString("Press Z to stop wheel", 350, 100);
        gfx.drawString("Press X to cancel", 350, 110);
    }
}
