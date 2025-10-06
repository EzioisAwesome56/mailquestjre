package com.eziosoft.mailquestjre.renderObjects;

import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderObjects.DrawableObject;
import com.alysoft.dankengine.renderer.DankColor;
import com.alysoft.dankengine.renderer.DankFont;

public class ItemMenuTabRenderer implements DrawableObject {
    private int tab;
    private boolean solid_arrow = true;

    public ItemMenuTabRenderer(int tab_no){
        this.tab = tab_no;
    }
    public void disableSolid(){
        this.solid_arrow = false;
    }

    @Override
    public void drawObject(GraphicsBackend gfx) {
       // set color and draw one really long box
        gfx.drawRectangleFilled(0, 0, 500, 50, DankColor.pink);
        // draw 3 smaller inner rectangles
        gfx.drawRectangleFilled(8, 8, 150, 34, DankColor.lightGray);
        gfx.drawRectangleFilled(174, 8, 150, 34, DankColor.lightGray);
        gfx.drawRectangleFilled(340, 8, 151, 34, DankColor.lightGray);
        // set font and color
        DankFont thefont = new DankFont("helvetica", 0, 25);
        // draw "Items" text
        gfx.drawTextSimple(32, 34, thefont, DankColor.black, "Items");
        // draw weapons text
        gfx.drawTextSimple(200, 34, thefont, DankColor.black, "Weapons");
        // draw key items textt
        gfx.drawTextSimple(370, 34, thefont, DankColor.black, "Key Item");
        // change color and draw selection arrow
        if (this.solid_arrow){
            // draw it solid
            gfx.drawPolygonFilled(new int[]{10 + (this.tab * 166), 10 + (this.tab * 166), 25 + (this.tab * 166)}, new int[]{10, 40, 25}, 3, DankColor.green);
        } else {
            // draw it without filling it in
            gfx.drawPolygon(new int[]{10 + (this.tab * 166), 10 + (this.tab * 166), 25 + (this.tab * 166)}, new int[]{10, 40, 25}, 3, DankColor.green);
        }
    }
}
