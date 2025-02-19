package com.eziosoft.mailquestjre.renderObjects;

import java.awt.*;

public class SortObject implements DrawableObject{
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
    public void drawObject(Graphics2D gfx) {
        // set color based on variable
        if (this.isRed){
            gfx.setColor(Color.RED);
            // draw a 50x50 box
            gfx.fillRect(this.x, this.y, 50, 50);
        } else {
            gfx.setColor(Color.BLUE);
            // draw a circle
            gfx.fillOval(this.x, this.y, 50, 50);
        }
    }
}
