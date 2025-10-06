package com.eziosoft.mailquestjre.renderObjects;

import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderObjects.DrawableObject;
import com.alysoft.dankengine.renderer.DankColor;
public class SortObject implements DrawableObject {
    // variables
    private boolean isRed;
    private int x;
    private int y;

    // object constructor
    public SortObject(boolean color, int x, int y){
        this.isRed = color;
        this.x = x;
        this.y = y;
    }
    // methods
    public void addToX(int add){
        this.x += add;
    }
    public int getX(){
        return this.x;
    }
    public boolean isRed(){
        return this.isRed;
    }


    @Override
    public void drawObject(GraphicsBackend gfx) {
        // set color based on variable
        if (this.isRed){
            // draw a 50x50 box
            gfx.drawRectangleFilled(this.x, this.y, 50, 50, DankColor.red);
        } else {
            // draw a circle
            gfx.drawCircleFilled(this.x, this.y, 50, DankColor.blue);
        }
    }
}
