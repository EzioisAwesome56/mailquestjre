package com.eziosoft.mailquestjre.renderObjects;

import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderObjects.DrawableObject;
import com.alysoft.dankengine.renderer.DankColor;
import com.alysoft.dankengine.renderer.DankFont;
import com.eziosoft.mailquestjre.stuff.ItemMenuListItem;

import java.util.ArrayList;

public class ItemMenuListRenderer implements DrawableObject {
    private ArrayList<ItemMenuListItem> items;
    private int selected_item;

    // object constructor, provide list of items here. Note you may need to provide a sublist if there are too many
    public ItemMenuListRenderer(ArrayList<ItemMenuListItem> items, int sel){
        this.items = items;
        this.selected_item = sel;
    }

    @Override
    public void drawObject(GraphicsBackend gfx) {
        // draw outer box first
        // set the color for that
        gfx.drawRectangleFilled(0, 60, 500, 440, DankColor.pink);
        // draw inner box
        gfx.drawRectangleFilled(8, 68, 484, 424, DankColor.lightGray);
        // setup font for rendering
        DankFont thefont = new DankFont("helvetica", 0, 27);
        // draw all the items in the list
        for (int item = 0; item < this.items.size(); item++){
            // check what type of item it is
            switch (this.items.get(item).getQuanity()){
                case -2: // this is a weapon
                    this.drawWeaponItem(gfx, item, this.items.get(item), thefont);
                    break;
                case -1: // this is a key item
                    this.drawKeyItem(gfx, item, this.items.get(item), thefont);
                    break;
                default: // TODO: regular item
            }
        }
        // draw selected arrow
        gfx.drawPolygonFilled(new int[]{480, 480, 460}, new int[]{70 + (this.selected_item * 30), 100 + (this.selected_item * 30), 85 + (this.selected_item * 30)}, 3, DankColor.green);
    }

    // draw a weapon item entry to the list
    private void drawWeaponItem(GraphicsBackend gfx, int y_offset, ItemMenuListItem ctx, DankFont font){
        // reset color to black
        // draw string of item name to the board
        gfx.drawTextSimple(10, 90 + (y_offset * 30), font, DankColor.black, ctx.getName() + " -- DMG=" + ctx.getDmg());
        if (ctx.isEquipped()){
            // draw a blue circle next to it's menu item
            gfx.drawCircleFilled(400, 70 +  (y_offset * 30), 20, DankColor.blue);
        }
    }
    // draw a key item entry to the list
    private void drawKeyItem(GraphicsBackend gfx, int y_offset, ItemMenuListItem ctx, DankFont font){
        // reset color to black
        // draw string of item name to the board
        gfx.drawTextSimple(10, 90 + (y_offset * 30), font, DankColor.black, ctx.getName());
    }
    // draw a regular item
    private void drawRegularItem(GraphicsBackend gfx, int y_offset, ItemMenuListItem ctx){
        // TODO: once items are in, come back and write this
        throw new RuntimeException("How did you even call this?");
    }
}
