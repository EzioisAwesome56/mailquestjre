package com.eziosoft.mailquestjre.stuff.enums;

public enum PlayerWeapons {
    UNARMED(1, "UnArmed"),
    BAT(3, "Baseball Bat");


    public final int dmg;
    public final String name;
    PlayerWeapons(int dmg, String name){
        this.dmg = dmg;
        this.name = name;
    }

}
