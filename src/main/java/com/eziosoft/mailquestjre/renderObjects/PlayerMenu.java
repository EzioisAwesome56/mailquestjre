package com.eziosoft.mailquestjre.renderObjects;

import java.awt.*;

public class PlayerMenu implements DrawableObject {

    private final int itemselected;

    public PlayerMenu(int item){
        this.itemselected = item;
    }


    @Override
    public void drawObject(Graphics2D gfx) {
        // set outer color
        gfx.setColor(Color.pink);
        // draw rectangle
        gfx.fillRect(300, 0, 200, 190);
        // change color
        gfx.setColor(Color.lightGray);
        // draw inner rectangle
        gfx.fillRect(308, 8, 184, 174);
        // setup the font rendering
        gfx.setColor(Color.BLACK);
        gfx.setFont(new Font("helvetica", Font.PLAIN, 27));
        // draw the 0th menu item
        gfx.drawString("Your Stats", 330, 35);
        // etc draw all the others
        gfx.drawString("Items", 330, 65);
        gfx.drawString("Map", 330, 95);
        gfx.drawString("Save", 330, 125);
        gfx.drawString("Quit", 330, 155);
        // TODO: if anything else needs drawn, do so
        // next: draw arrow
        // we need to calculate how far down to move it
        int addy = 30 * this.itemselected;
        gfx.setColor(Color.green);
        gfx.fillPolygon(new int[]{310, 310, 325}, new int[]{10 + addy, 40 + addy, 24 + addy}, 3);
    }
}
