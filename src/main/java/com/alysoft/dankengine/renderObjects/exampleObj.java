package com.alysoft.dankengine.renderObjects;

import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderer.DankColor;

public class exampleObj implements DrawableObject {

    private int x;
    private int y;
    private int counter;

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public exampleObj(){
        this.x = 0;
        this.y = 0;
        this.counter = 0;
    }

    public void tryToMoveRight(){
        if (this.counter == 13){
            this.x += 5;
            this.counter = 0;
        } else {
            this.counter++;
        }
    }
    public void tryToMoveLeft(){
        if (this.counter == 13){
            this.x -= 5;
            this.counter = 0;
        } else {
            this.counter++;
        }
    }

    @Override
    public void drawObject(GraphicsBackend gfx) {
        // draw a square
        gfx.drawRectangleFilled(this.x, this.y, 50, 50, DankColor.magenta);
    }
}
