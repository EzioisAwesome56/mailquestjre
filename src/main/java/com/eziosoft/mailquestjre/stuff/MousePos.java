package com.eziosoft.mailquestjre.stuff;

public class MousePos {

    private boolean inPanel;
    private int x;
    private int y;

    public MousePos(int x, int y){
        this.x = x;
        this.y = y;
        this.inPanel = true;
    }

    public MousePos(){
        this.x = 0;
        this.y = 0;
        this.inPanel = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isInPanel() {
        return inPanel;
    }
}
