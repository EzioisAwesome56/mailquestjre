package com.eziosoft.mailquestjre.renderObjects;

import java.awt.*;
import java.util.ArrayList;

public class TestMousePoint implements DrawableObject{

    private ArrayList<int[]> points = new ArrayList<>();

    public void addPoint(int x, int y){
        int[] bruh = new int[]{x, y};
        this.points.add(bruh);
    }

    @Override
    public void drawObject(Graphics2D gfx) {
        for (int[] pair : this.points){
            gfx.setColor(Color.magenta);
            gfx.fillRect(pair[0], pair[1], 1, 1);
        }
    }
}
