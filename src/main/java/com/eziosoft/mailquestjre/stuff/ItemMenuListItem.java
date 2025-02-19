package com.eziosoft.mailquestjre.stuff;

import com.eziosoft.mailquestjre.stuff.enums.PlayerWeapons;

public class ItemMenuListItem {
    private String name;
    private boolean isEquipped;
    /*
    this is used for checking what kind of item it actually is
    -2 -> weapon
    -1 -> key item
    >=0 -> regular items
     */
    private int quanity;
    private int dmg;

    public ItemMenuListItem(PlayerWeapons wep, boolean equip){
        this.name = wep.name;
        this.isEquipped = equip;
        this.dmg = wep.dmg;
        this.quanity = -2;
    }
    public ItemMenuListItem(String keyitemname){
        this.name = keyitemname;
        this.quanity = -1;
    }
    // TODO: regular items do not exit yet. Add them to here at some point



    public String getName() {
        return this.name;
    }

    public int getDmg() {
        return this.dmg;
    }

    public int getQuanity() {
        return this.quanity;
    }

    public boolean isEquipped() {
        return this.isEquipped;
    }
}
