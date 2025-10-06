package com.eziosoft.mailquestjre.renderObjects;

import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderObjects.DrawableObject;
import com.alysoft.dankengine.renderer.DankGraphic;

public class SimpleImageRenderer implements DrawableObject {
    private final int x;
    private final int y;
    private final DankGraphic content;

    public SimpleImageRenderer(DankGraphic img){
        this(img, 0, 0);
    }
    public SimpleImageRenderer(DankGraphic img, int x, int y){
        this.content = img;
        this.x = x;
        this.y = y;
    }

    @Override
    public void drawObject(GraphicsBackend gfx) {
        // draw using the image and x/y coords
        this.content.drawGraphic(this.x, this.y, gfx);
    }
}
