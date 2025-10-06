package com.alysoft.dankengine.renderer;

import com.alysoft.dankengine.backends.base.GraphicsBackend;

public abstract class DankGraphic {

    int height;
    int width;
    boolean transparency;

    public DankGraphic(int height, int width, boolean transparency){
        this.height = height;
        this.width = width;
        this.transparency = transparency;
    }

    public DankGraphic() {
        // nothing, but we need this here for later maybe
    }

    public abstract void drawGraphic(int x, int y, GraphicsBackend gfx);
    public abstract GraphicsBackend getDrawable();
    public abstract void flush();
}
