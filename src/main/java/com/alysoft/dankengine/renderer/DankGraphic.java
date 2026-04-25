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
    // these are required for some rendering stuff later, so
    // we can implement them so we can access the values
    public int getHeight(){
        return this.height;
    }
    public int getWidth(){
        return this.width;
    }
    public boolean isTransparent(){
        return this.transparency;
    }

    public abstract void drawGraphic(int x, int y, GraphicsBackend gfx);
    public abstract GraphicsBackend getDrawable();
    public abstract void flush();

    /**
     * USE WITH CAUTION! only really intended to be used in the backend where you can know
     * what type to expect. using it within game code will make non-portable code!
     * @return the raw image data stored within.
     */
    public abstract Object getRawData();
}
