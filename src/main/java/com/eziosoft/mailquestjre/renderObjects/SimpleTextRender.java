package com.eziosoft.mailquestjre.renderObjects;

import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderObjects.DrawableObject;
import com.alysoft.dankengine.renderer.DankColor;
import com.alysoft.dankengine.renderer.DankFont;
public class SimpleTextRender implements DrawableObject {
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
    public void drawObject(GraphicsBackend gfx) {
        // set graphics2d font and color
        // draw content
        gfx.drawTextSimple(this.x, this.y, new DankFont("helvetica", 0, 27), DankColor.black, this.content);
    }
}
