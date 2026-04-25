package com.alysoft.dankengine.utility;

/**
 * for scenes that do not scroll, using this may make more sense.
 * it does not scroll at all, and always returns 0 for both x and y
 */
public class ZeroCamera implements Camera{
    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public void followPointX(int xpos, int min, int max) {
        // do absolutely nothing
    }

    @Override
    public void followPointY(int y, int min, int max) {
        // also do absolutely nothing
    }
}
