package com.eziosoft.mailquestjre.renderObjects;

import java.awt.*;

public class BattleExclaim implements DrawableObject{
    private int tilex;
    private int tiley;

    public BattleExclaim(int x, int y){
        this.tilex = x;
        this.tiley = y;
    }

    @Override
    public void drawObject(Graphics2D gfx) {
        // draw the outline of the box
        gfx.setColor(Color.black);
        gfx.drawRect(25 * this.tilex, 25 * this.tiley, 25, 25);
        // change color and draw inner box
        gfx.setColor(Color.yellow);
        gfx.fillRect((25 * this.tilex) + 1, (25 * this.tiley) + 1, 24, 24);
        // change color and set font
        gfx.setColor(Color.black);
        gfx.setFont(new Font("helvetica", Font.PLAIN, 23));
        gfx.drawString("!", (25 * this.tilex) + 9, (25 * this.tiley) + 20);
    }
}
