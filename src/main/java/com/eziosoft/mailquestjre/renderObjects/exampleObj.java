package com.eziosoft.mailquestjre.renderObjects;

import java.awt.*;

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
    public void drawObject(Graphics2D gfx) {
        // draw a square
        gfx.setColor(Color.MAGENTA);
        gfx.fillRect(this.x, this.y, 50, 50);
    }
}
