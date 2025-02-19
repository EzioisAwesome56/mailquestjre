package com.eziosoft.mailquestjre.stuff.enums;

public enum WarpActivations {
    BUTTON(0),
    RIGHT(1),
    LEFT(2),
    DOWN(3),
    UP(4);

    public int id;

    WarpActivations(int id){
        this.id = id;
    }
}
