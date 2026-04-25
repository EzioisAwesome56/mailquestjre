package com.alysoft.dankengine.utility;

public class ScrollCamera implements Camera {
    // borrowed from dove branch of untitled platformer game
    private int x;
    private int y;
    private int width;
    private int height;

    public ScrollCamera(int width, int height){
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public void followPointX(int position, int min, int max) {
        // the camera must stay confined within the given range
        this.x = Math.min(Math.max(position - (this.width / 2), min), max - this.width);
    }

    /**
     * Copy of followPointX, but for the Y axis instead
     * @param y current y position
     * @param min minimum y value
     * @param max maximum y value
     */
    @Override
    public void followPointY(int y, int min, int max) {
        this.y = Math.min(Math.max(y - (this.height / 2), min), max - this.height);
    }
}
