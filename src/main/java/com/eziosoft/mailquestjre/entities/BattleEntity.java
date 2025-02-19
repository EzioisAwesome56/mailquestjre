package com.eziosoft.mailquestjre.entities;

public interface BattleEntity {

    // define all methods here
    int getHealth();
    int getMaxHealth();
    int getAtk();
    int getDef();
    int getMagic();
    int getMaxMagic();
    int getLevel();
    String getName();
    int getMissRate();
    // stuff for later
    void takeDamage(int damage);
    void heal(int heal);
    int performRegularAttack(BattleEntity target);
    int performMagicAttack(BattleEntity Target);
    void addAtk(int add);
    void addDef(int add);
    void addHP(int add);
    void addMagic(int add);
    void increaseLevel();
}
