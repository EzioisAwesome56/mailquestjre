package com.eziosoft.mailquestjre.json;

import java.util.List;

public class OverworldMap {

    /*
    a map object is composed of several pieces
    - name of map tilemap, probably from tiled
    - tileset used
    - name of the map
    - list of events, with whatever that means
    - warps to other maps
    - wandering NPCs TODO
    - script to run on map load (set to "null" for no script)
    - is this a valid last heal map?
    - x and y coords of the last heal respawn place
    - valid spawn locations
    - encounter mode: 0 -> none, 1 -> only in grass, 2  -> everywhere
    - encounter table
    - wild encounter rng base number
    - todo: flesh out the rest of this list
     */
    private String tilemap;
    private String tileset;
    private String mapname;
    private List<MapEvent> events;
    private List<MapWarp> warps;
    // TODO: NPCS
    private String load_script;
    private boolean validheal;
    @Deprecated
    private int heal_x;
    @Deprecated
    private int heal_y;
    private int heal_spawn;
    private List<SpawnPoint> spawns;
    private int encountermode;
    private List<EncounterTableEntry> encounters;
    private int wildchance;


    // getters for all of these
    public int getWildChance() {
        return this.wildchance;
    }

    public List<MapWarp> getWarps() {
        return this.warps;
    }
    public int getEncountermode() {
        return this.encountermode;
    }
    public List<EncounterTableEntry> getEncounters() {
        return this.encounters;
    }
    public String getTilemap() {
        return this.tilemap;
    }
    public int getHeal_y() {
        return this.heal_y;
    }
    public List<SpawnPoint> getSpawns() {
        return this.spawns;
    }
    public int getHeal_x() {
        return this.heal_x;
    }
    public boolean isValidheal() {
        return this.validheal;
    }
    public String getLoad_script() {
        return this.load_script;
    }
    public String getMapname() {
        return this.mapname;
    }
    public String getTileset() {
        return this.tileset;
    }
    public List<MapEvent> getEvents() {
        return this.events;
    }

    public int getHeal_spawn() {
        return this.heal_spawn;
    }
}
