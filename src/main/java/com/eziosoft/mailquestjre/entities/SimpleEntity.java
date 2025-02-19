package com.eziosoft.mailquestjre.entities;

import com.eziosoft.mailquestjre.Main;
import com.eziosoft.mailquestjre.json.FightableEntity;

public class SimpleEntity implements BattleEntity {
    // lay down everything we need
    private int hp;
    private int maxhp;
    private int magic;
    private int maxmagic;
    private String name;
    private int def;
    private int atk;
    private int level;
    private int missrate;

    //  object constructor
    public SimpleEntity(FightableEntity ent){
        // this object contains most of what we need
        this.atk = ent.getAtk();
        this.maxhp  = ent.getHp();
        this.hp = ent.getHp();
        this.magic = ent.getMagic();
        this.maxmagic = ent.getMagic();
        this.name = ent.getName();
        this.level = 1;
        this.def = ent.getDef();
        this.missrate = ent.getMissrate();
    }

    @Override
    public int getHealth() {
        return this.hp;
    }
    @Override
    public int getMaxHealth() {
        return this.maxhp;
    }
    @Override
    public int getAtk() {
        return this.atk;
    }
    @Override
    public int getDef() {
        return this.def;
    }

    @Override
    public int getMagic() {
        return this.magic;
    }
    @Override
    public int getMaxMagic() {
        return this.maxmagic;
    }
    @Override
    public int getLevel() {
        return this.level;
    }
    @Override
    public String getName() {
        return this.name;
    }
    @Override
    public int getMissRate() {
        return this.missrate;
    }
    @Override
    public void takeDamage(int damage) {
        this.hp -= damage;
    }
    @Override
    public void heal(int heal) {
        // todo: this may not end up doing anything actually
    }
    @Override
    public int performRegularAttack(BattleEntity target) {
        // first, preform rng on if we miss or not
        assert this.missrate > 0;
        int miss = Main.random.nextInt(this.missrate);
        if (miss % 4 == 3){
            // you missed, dumbass
            return -1;
        }
        // otherwise, calculate damage based on your attack and their defense
        // simpleentities dont have equipable weapons, so no special bonus is required
        int dmg = ((this.atk) - (target.getDef() - Main.random.nextInt(3))) + 1;
        // ensure you do atleast 1 damage
        if (dmg <= 0) dmg = 1;
        return dmg;
    }
    @Override
    public int performMagicAttack(BattleEntity Target) {
        return 0;
    }

    @Override
    public void addAtk(int add) {
        this.atk += add;
    }

    @Override
    public void addDef(int add) {
        this.def += add;
    }

    @Override
    public void addHP(int add) {
        this.maxhp += add;
        this.hp = this.maxhp;
    }

    @Override
    public void addMagic(int add) {
        this.maxmagic += add;
        this.magic = this.maxmagic;
    }

    @Override
    public void increaseLevel() {
        // increase by one
        this.level++;
    }
}
