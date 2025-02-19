package com.eziosoft.mailquestjre.renderObjects;

import java.awt.*;

public class ItemMenuTabRenderer implements DrawableObject{
    private int tab;
    private boolean solid_arrow = true;

    public ItemMenuTabRenderer(int tab_no){
        this.tab = tab_no;
    }
    public void disableSolid(){
        this.solid_arrow = false;
    }

    @Override
    public void drawObject(Graphics2D gfx) {
       // set color and draw one really long box
        gfx.setColor(Color.pink);
        gfx.fillRect(0, 0, 500, 50);
        // draw 3 smaller inner rectangles
        gfx.setColor(Color.lightGray);
        gfx.fillRect(8, 8, 150, 34);
        gfx.fillRect(174, 8, 150, 34);
        gfx.fillRect(340, 8, 151, 34);
        // set font and color
        gfx.setColor(Color.BLACK);
        gfx.setFont(new Font("helvetica", Font.PLAIN, 25));
        // draw "Items" text
        gfx.drawString("Items", 32, 34);
        // draw weapons text
        gfx.drawString("Weapons", 200, 34);
        // draw key items textt
        gfx.drawString("Key Item", 370, 34);
        // change color and draw selection arrow
        gfx.setColor(Color.green);
        if (this.solid_arrow){
            // draw it solid
            gfx.fillPolygon(new int[]{10 + (this.tab * 166), 10 + (this.tab * 166), 25 + (this.tab * 166)}, new int[]{10, 40, 25}, 3);
        } else {
            // draw it without filling it in
            gfx.drawPolygon(new int[]{10 + (this.tab * 166), 10 + (this.tab * 166), 25 + (this.tab * 166)}, new int[]{10, 40, 25}, 3);
        }
    }
}
