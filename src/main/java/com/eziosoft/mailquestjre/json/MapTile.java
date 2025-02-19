package com.eziosoft.mailquestjre.json;

public class MapTile {

    // properties of the map tile go here
    // there are really only two things required for this
    private String graphics;
    private boolean walkable;

    public boolean isWalkable() {
        return walkable;
    }

    public String getGraphics() {
        return graphics;
    }
}
