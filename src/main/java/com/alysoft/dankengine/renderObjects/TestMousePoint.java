package com.alysoft.dankengine.renderObjects;

import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderer.DankColor;

import java.util.ArrayList;

public class TestMousePoint implements DrawableObject{

    private ArrayList<int[]> points = new ArrayList<>();

    public void addPoint(int x, int y){
        int[] bruh = new int[]{x, y};
        this.points.add(bruh);
    }

    @Override
    public void drawObject(GraphicsBackend gfx) {
        for (int[] pair : this.points){
            gfx.drawRectangleFilled(pair[0], pair[1], 1, 1, DankColor.magenta);
        }
    }
}
