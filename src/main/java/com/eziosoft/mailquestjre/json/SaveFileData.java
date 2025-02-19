package com.eziosoft.mailquestjre.json;

import com.eziosoft.mailquestjre.stuff.Player;

import java.util.HashMap;

public class SaveFileData {

    // this data is what is actually inside the save file
    /*
    Save File Versions exist to help smooth over
    carrying save files over from previous versions
    versions known:
    1 -> MailQuest Demo 1
     */
    private int version;
    // store the player object
    private Player player;
    private HashMap<String, Object> flags;
    private String current_map;
    private int x;
    private int y;

    // getters and setters;
    public HashMap<String, Object> getFlags() {
        return this.flags;
    }
    public void setFlags(HashMap<String, Object> flags) {
        this.flags = flags;
    }
    public Player getPlayer() {
        return this.player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }
    public int getVersion() {
        return this.version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
    public int getY() {
        return this.y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getX() {
        return this.x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public String getCurrent_map() {
        return this.current_map;
    }
    public void setCurrent_map(String current_map) {
        this.current_map = current_map;
    }
}
