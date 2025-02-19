package com.eziosoft.mailquestjre.stuff.enums;

public enum GameStates {
    // TODO: if rest of states are added, get them here
    OVERWORLD(0),
    BATTLE(1),
    TITLE(2),
    OBJSORT(3),
    STATS(4),
    ITEMS(5),
    CUTSCENE(6);


    public final int id;
    private GameStates(int id){
        this.id = id;
    }
}
