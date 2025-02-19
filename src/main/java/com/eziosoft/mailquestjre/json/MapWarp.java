package com.eziosoft.mailquestjre.json;

public class MapWarp {

    /**
     * warps are fairly simple to define and use
     * they are as followed
     * - Destination map
     * - Destination spawn point
     * - warp tile x
     * - warp tile y
     * - activation type
     * which is one of
     * 0 - press action button
     * 1 - press right while on tile
     * 2 - press left while on tile
     * 3 - press down while on tile
     * 4 - press up while on tile
     */
    private String destination;
    private int spawnpoint;
    private int tilex;
    private int tiley;
    private int activation_type;

    public String getDestination() {
        return this.destination;
    }

    public int getTilex() {
        return this.tilex;
    }

    public int getSpawnpoint() {
        return this.spawnpoint;
    }

    public int getTiley() {
        return this.tiley;
    }

    public int getActivation_type() {
        return this.activation_type;
    }
}
