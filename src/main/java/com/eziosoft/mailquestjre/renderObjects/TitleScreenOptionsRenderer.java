package com.eziosoft.mailquestjre.renderObjects;

import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderObjects.DrawableObject;
import com.alysoft.dankengine.renderer.DankColor;
import com.alysoft.dankengine.renderer.DankFont;

public class TitleScreenOptionsRenderer implements DrawableObject {

    // stuff for later
    private boolean show_continue;
    private int option_selected;

    // static variables
    private static int xpos = 150;

    public TitleScreenOptionsRenderer(boolean show_continue, int option){
        this.show_continue = show_continue;
        this.option_selected = option;
    }

    @Override
    public void drawObject(GraphicsBackend gfx) {
        // setup graphics2d correctly
        gfx.setupDrawString(new DankFont("helvetica", 0, 27), DankColor.black);
        /*
        the first option is continue if a save file is present
        if one isnt present, this code chunk will be skipped over
         */
        int basey = 200;
        if (this.show_continue){
            gfx.drawString("Continue", xpos, basey);
            basey += 30;
            gfx.drawString("New Game", xpos, basey);
            basey += 30;
        } else {
            gfx.drawString("New Game", xpos, basey);
            basey += 30;
        }
        // draw options next
        gfx.drawString("Options", xpos, basey);
        basey += 30;
        // draw erase save file
        gfx.drawString("Erase Save File", xpos, basey);
        basey += 30;
        // finally, draw credits
        gfx.drawString("Credits", xpos, basey);
        // then, we have to draw the selection arrow; pain and suffering
        gfx.drawPolygonFilled(new int[]{130, 130, 145}, new int[]{180 + (30 * this.option_selected), 200 + (30 * this.option_selected), 190 + (30 * this.option_selected)}, 3, DankColor.green);
    }
}
