package com.eziosoft.mailquestjre.stuff.enums;

public enum EventTypes {
    TEXT(0),
    PREDEF(1),
    SCRIPT(2),
    CUTSCENE(3);

    public int id;

    EventTypes(int id){
        this.id = id;
    }
}
