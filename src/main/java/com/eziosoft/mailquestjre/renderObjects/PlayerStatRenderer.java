package com.eziosoft.mailquestjre.renderObjects;

import com.eziosoft.mailquestjre.stuff.Player;

import java.awt.*;

public class PlayerStatRenderer implements DrawableObject{
    private Player player;

    public PlayerStatRenderer(Player p){
        this.player = p;
    }


    @Override
    public void drawObject(Graphics2D gfx) {
        // set color and draw outer box
        gfx.setColor(Color.pink);
        gfx.fillRect(0, 0, 300, 500);
        // switch color and draw inner box
        gfx.setColor(Color.lightGray);
        gfx.fillRect(8, 8, 284, 484);
        // set color and font
        gfx.setColor(Color.black);
        gfx.setFont(new Font("helvetica", Font.PLAIN, 27));
        // draw all stats about the player
        gfx.drawString("Name: " + this.player.getName(), 9, 34);
        gfx.drawString("HP: " + this.player.getHealth() + "/" + this.player.getMaxHealth(), 9, 64);
        gfx.drawString("MP: " + this.player.getMagic() + "/" + this.player.getMaxMagic(), 9, 94);
        gfx.drawString("ATK: " + this.player.getAtk(), 9, 124);
        gfx.drawString("DEF: " + this.player.getDef(), 9, 154);
        gfx.drawString("Money: " + this.player.getMoney(), 9, 184);
        gfx.drawString("EXP: " + this.player.getExp(), 9, 214);
        gfx.drawString("Needed: " + (this.player.getNext_exp() - this.player.getExp()), 9, 244);
        gfx.drawString("Weapon in use: ", 9, 274);
        gfx.drawString(this.player.getWeapon().name, 9, 304);
        gfx.drawString("# Weapons: " + this.player.getUnlocked_weapons().size(), 9, 334);
        gfx.drawString("# Key Items: " + this.player.getKeyitems().size(), 9, 364);
        gfx.drawString("Miss Chance: " + this.player.getMissRate(), 9, 394);
        gfx.drawString("Level: " + this.player.getLevel(), 9, 424);
        // damn, we ran out of stuff to show
    }
}
