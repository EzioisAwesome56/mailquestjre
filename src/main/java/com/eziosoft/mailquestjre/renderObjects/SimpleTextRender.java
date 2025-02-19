package com.eziosoft.mailquestjre.renderObjects;

import java.awt.*;

public class SimpleTextRender implements DrawableObject{
    // stuff for later
    private String content;
    private int x;
    private int y;

    public SimpleTextRender(String content, int x, int y){
        this.content = content;
        this.x = x;
        this.y = y;
    }
    public SimpleTextRender(int content, int x, int y){
        this(Integer.toString(content), x, y);
    }


    @Override
    public void drawObject(Graphics2D gfx) {
        // set graphics2d font and color
        gfx.setColor(Color.black);
        gfx.setFont(new Font("helvetica", Font.PLAIN, 27));
        // draw content
        gfx.drawString(this.content, this.x, this.y);
    }
}
