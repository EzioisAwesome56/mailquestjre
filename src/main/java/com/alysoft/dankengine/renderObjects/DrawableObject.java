package com.alysoft.dankengine.renderObjects;

import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.utility.Camera;

public interface DrawableObject {

    void drawObject(GraphicsBackend gfx, Camera camera);
}
