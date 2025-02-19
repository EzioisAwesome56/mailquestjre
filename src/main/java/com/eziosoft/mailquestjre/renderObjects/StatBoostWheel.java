package com.eziosoft.mailquestjre.renderObjects;

import java.awt.*;

public class StatBoostWheel implements DrawableObject{
    private final int number;

    public StatBoostWheel(int num){
        this.number = num;
    }


    @Override
    public void drawObject(Graphics2D gfx) {
        // set color and draw outer box
        gfx.setColor(Color.pink);
        gfx.fillRect(400, 4, 60, 70);
        // draw inner box
        gfx.setColor(Color.lightGray);
        gfx.fillRect(408, 12, 44, 54);
        // setup font and draw number
        gfx.setColor(Color.black);
        gfx.setFont(new Font("helvetica", Font.PLAIN, 50));
        gfx.drawString(Integer.toString(this.number), 412, 54);
        // give the player a hint
        gfx.setFont(new Font("helvetica", Font.PLAIN, 13));
        gfx.drawString("Press Z to stop wheel", 350, 100);
        gfx.drawString("Press X to cancel", 350, 110);
    }
}
