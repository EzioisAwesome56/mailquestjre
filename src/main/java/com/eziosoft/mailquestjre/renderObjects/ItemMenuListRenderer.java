package com.eziosoft.mailquestjre.renderObjects;

import com.eziosoft.mailquestjre.stuff.ItemMenuListItem;

import java.awt.*;
import java.util.ArrayList;

public class ItemMenuListRenderer implements DrawableObject{
    private ArrayList<ItemMenuListItem> items;
    private int selected_item;

    // object constructor, provide list of items here. Note you may need to provide a sublist if there are too many
    public ItemMenuListRenderer(ArrayList<ItemMenuListItem> items, int sel){
        this.items = items;
        this.selected_item = sel;
    }

    @Override
    public void drawObject(Graphics2D gfx) {
        // draw outer box first
        // set the color for that
        gfx.setColor(Color.pink);
        gfx.fillRect(0, 60, 500, 440);
        // draw inner box
        gfx.setColor(Color.lightGray);
        gfx.fillRect(8, 68, 484, 424);
        // setup font for rendering
        gfx.setColor(Color.BLACK);
        gfx.setFont(new Font("helvetica", Font.PLAIN, 27));
        // draw all the items in the list
        for (int item = 0; item < this.items.size(); item++){
            // check what type of item it is
            switch (this.items.get(item).getQuanity()){
                case -2: // this is a weapon
                    this.drawWeaponItem(gfx, item, this.items.get(item));
                    break;
                case -1: // this is a key item
                    this.drawKeyItem(gfx, item, this.items.get(item));
                    break;
                default: // TODO: regular item
            }
        }
        // draw selected arrow
        gfx.setColor(Color.green);
        gfx.fillPolygon(new int[]{480, 480, 460}, new int[]{70 + (this.selected_item * 30), 100 + (this.selected_item * 30), 85 + (this.selected_item * 30)}, 3);
    }

    // draw a weapon item entry to the list
    private void drawWeaponItem(Graphics2D gfx, int y_offset, ItemMenuListItem ctx){
        // reset color to black
        gfx.setColor(Color.BLACK);
        // draw string of item name to the board
        gfx.drawString(ctx.getName() + " -- DMG=" + ctx.getDmg(), 10, 90 + (y_offset * 30));
        if (ctx.isEquipped()){
            // draw a blue circle next to it's menu item
            gfx.setColor(Color.BLUE);
            gfx.fillOval(400, 70 +  (y_offset * 30), 20, 20);
        }
    }
    // draw a key item entry to the list
    private void drawKeyItem(Graphics2D gfx, int y_offset, ItemMenuListItem ctx){
        // reset color to black
        gfx.setColor(Color.BLACK);
        // draw string of item name to the board
        gfx.drawString(ctx.getName(), 10, 90 + (y_offset * 30));
    }
    // draw a regular item
    private void drawRegularItem(Graphics2D gfx, int y_offset, ItemMenuListItem ctx){
        // TODO: once items are in, come back and write this
        throw new RuntimeException("How did you even call this?");
    }
}
