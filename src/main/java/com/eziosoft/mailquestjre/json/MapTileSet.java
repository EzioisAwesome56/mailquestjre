package com.eziosoft.mailquestjre.json;

import java.util.HashMap;

public class MapTileSet {

    // this is used to read in the json file that contains a tileset
    // the tiles are stored in a hashmap with their ID in tiled as the key
    // and a MapTile Object as the value
    private HashMap<Integer, MapTile> tiles;
    private String tileset_name;
    // base folder in resouces folder for storing the graphics assets
    private String basefolder;

    public HashMap<Integer, MapTile> getTiles() {
        return this.tiles;
    }

    public String getBasefolder() {
        return this.basefolder;
    }

    public String getTileset_name() {
        return this.tileset_name;
    }
}
