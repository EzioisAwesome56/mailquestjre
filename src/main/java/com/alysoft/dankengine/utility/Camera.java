package com.alysoft.dankengine.utility;

public interface Camera {
    int getX();
    int getY();
    void followPointX(int xpos, int min, int max);
    void followPointY(int y, int min, int max);
}
