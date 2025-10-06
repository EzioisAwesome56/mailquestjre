package com.eziosoft.mailquestjre.stuff;

import com.eziosoft.mailquestjre.MailQuestJRE;
import com.eziosoft.mailquestjre.entities.BattleEntity;
import com.eziosoft.mailquestjre.stuff.enums.PlayerWeapons;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player implements BattleEntity {

    private String name;
    private int maxhp;
    private int hp;
    private int magic;
    private int maxmagic;
    private int atk;
    private int def;
    private int miss_rate;
    private int level;
    // non-battle related states
    private int money;
    private long exp;
    private long next_exp;
    private String last_heal_map;
    // TODO: inventory stuff
    // we have an equiped weapon var and unlocked weapon vars
    private PlayerWeapons equipped_weapon;
    private List<PlayerWeapons> unlocked_weapons;
    private ArrayList<String> keyitems;

    public Player(String name){
        // generate a new player
        this.name = name;
        this.level = 1;
        // we need RNG for this
        // edit 2-6-2025: switch to global random function
        Random rand = MailQuestJRE.random;
        // generate HP
        this.maxhp = Math.abs(rand.nextInt(3)) + 9;
        this.hp = this.maxhp;
        // magic
        this.maxmagic = Math.abs(rand.nextInt(8)) + 1;
        this.magic = this.maxmagic;
        // attack and defense
        this.atk = Math.abs(rand.nextInt(5)) + 4;
        this.def = Math.abs(rand.nextInt(3)) + 3;
        // miss rate
        this.miss_rate = Math.abs(rand.nextInt(5)) + 1;
        // money and exp
        this.money = 0;
        this.exp = 0;
        this.next_exp = 12;
        this.keyitems = new ArrayList<>();
        // set the last heal point to your house
        this.last_heal_map = "player_house";
        // give the player unarmed
        this.equipped_weapon = PlayerWeapons.UNARMED;
        this.unlocked_weapons = new ArrayList<>();
        this.unlocked_weapons.add(PlayerWeapons.UNARMED);
        // and we're done now
    }

    public String get_lasthealmap(){
        return this.last_heal_map;
    }
    public void set_lasthealmap(String map){
        this.last_heal_map = map;
    }
    @Override
    public String getName() {
        return this.name;
    }
    @Override
    public int getMissRate() {
        return this.miss_rate;
    }
    @Override
    public void takeDamage(int damage) {
        // subtract damage from our current HP
        this.hp -= damage;
    }
    @Override
    public void heal(int heal) {
        this.hp += heal;
    }
    @Override
    public int performRegularAttack(BattleEntity target) {
        // first, preform rng on if we miss or not
        assert this.miss_rate > 0;
        int miss = MailQuestJRE.random.nextInt(this.miss_rate);
        if (miss % 4 == 3){
            // you missed, dumbass
            return -1;
        }
        // otherwise, calculate damage based on your attack and their defense
        // also add bonus damage from equipped weapon
        int dmg = ((this.atk + this.equipped_weapon.dmg) - (target.getDef() - MailQuestJRE.random.nextInt(3))) + 1;
        // ensure you do atleast 1 damage
        if (dmg <= 0) dmg = 1;
        return dmg;
    }
    public void fully_heal(){
        this.hp = this.maxhp;
        this.magic = this.maxmagic;
    }
    @Override
    public int performMagicAttack(BattleEntity Target) {
        return 0;
    }

    // TODO: bounds check all of these to some sane value
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
    }
    @Override
    public void addMagic(int add) {
        this.maxmagic += add;
    }
    @Override
    public void increaseLevel() {
        // increase the player's level by one
        this.level += 1;
    }

    public ArrayList<String> getKeyitems() { return this.keyitems; }
    public PlayerWeapons getWeapon(){ return this.equipped_weapon; }
    public List<PlayerWeapons> getUnlocked_weapons(){ return this.unlocked_weapons; }
    // allow the ability to add a new weapon to the list
    public void UnlockNewWeapon(PlayerWeapons weapon){
        if (!this.unlocked_weapons.contains(weapon)) {
            this.unlocked_weapons.add(weapon);
        }
    }
    // allow equipped weapon to be changed
    public void equipNewWeapon(int index){ this.equipped_weapon = this.unlocked_weapons.get(index); }
    public long getExp() {
        return this.exp;
    }
    public void resetEXP(){ this.exp = 0; }
    public void gainExp(long gain){ this.exp += gain; }
    public long getNext_exp(){ return this.next_exp; }
    public void increaseEXPRequirements(){
        // take the current exp value, multiply by 2
        this.next_exp = this.next_exp * 2;
    }
    public int getMoney() {
        return this.money;
    }
    public void gainMoney(int amount){ this.money += amount; }
    @Override
    public int getDef() {
        return this.def;
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
    public int getMagic() {
        return this.magic;
    }
    @Override
    public int getMaxMagic() {
        return this.maxmagic;
    }
    @Override
    public int getLevel(){
        return this.level;
    }


}
