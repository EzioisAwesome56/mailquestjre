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
    /**
     * This flag will be set if the tileset was created before
     * - scrollable maps was implemented
     * - tile size was increased
     * this will cause the tiles to be lazily upscaled to account for this
     */
    private boolean legacy_tileset;

    public HashMap<Integer, MapTile> getTiles() {
        return this.tiles;
    }

    public String getBasefolder() {
        return this.basefolder;
    }

    public String getTileset_name() {
        return this.tileset_name;
    }

    @Deprecated // you probably shouldn't rely on this!
    public boolean isLegacy_tileset() {
        return this.legacy_tileset;
    }
}
