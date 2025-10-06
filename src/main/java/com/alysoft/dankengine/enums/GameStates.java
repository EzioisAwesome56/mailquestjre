package com.alysoft.dankengine.enums;

public enum GameStates {
    // TODO: rework this to manage game states without needing to edit this file
    TESTSTATE(0);


    public final int id;
    private GameStates(int id){
        this.id = id;
    }
}
