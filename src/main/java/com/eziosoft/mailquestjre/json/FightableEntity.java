package com.eziosoft.mailquestjre.json;

public class FightableEntity {

    private int hp;
    private int magic;
    private int atk;
    private int def;
    private String name;
    private String graphic;
    private int missrate;

    public int getHp() {
        return hp;
    }

    public int getMissrate(){
        return this.missrate;
    }
    public String getGraphic() {
        return graphic;
    }

    public int getDef() {
        return def;
    }

    public String getName() {
        return name;
    }

    public int getAtk() {
        return atk;
    }

    public int getMagic() {
        return magic;
    }
}
