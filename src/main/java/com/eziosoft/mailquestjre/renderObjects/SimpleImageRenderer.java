package com.eziosoft.mailquestjre.renderObjects;

import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderObjects.DrawableObject;
import com.alysoft.dankengine.renderer.DankGraphic;
import com.alysoft.dankengine.utility.Camera;
import com.alysoft.dankengine.utility.ZeroCamera;

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
    public void drawObject(GraphicsBackend gfx, Camera camera) {
        // draw using the image and x/y coords
        int drawx;
        int drawy;
        if (camera instanceof ZeroCamera){
            drawx = this.x;
            drawy = this.y;
        } else {
            drawx = -camera.getX() % this.content.getWidth();
            drawy = -camera.getY() % this.content.getHeight();
        }
        this.content.drawGraphic(drawx, drawy, gfx);
    }
}
