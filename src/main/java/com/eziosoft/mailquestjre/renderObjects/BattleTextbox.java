package com.eziosoft.mailquestjre.renderObjects;

import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderObjects.DrawableObject;
import com.alysoft.dankengine.renderer.DankColor;
import com.alysoft.dankengine.renderer.DankFont;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class BattleTextbox implements DrawableObject {
    // line break magic was borrowed from a very old oracle JDK example
    // see https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/2d/text/examples/LineBreakSample.java
    // and also https://docs.oracle.com/javase/tutorial/2d/text/drawmulstring.html

    private AttributedString content;
    private String content_raw;
    private boolean drawArrow = false;
    private boolean split_mode = false;
    // option selection things
    private boolean right_opt = false;
    private boolean bottom_row = false;
    private boolean forceempty = false;

    public BattleTextbox(String text){
        // give this to the attributed string
        this.content_raw = text;
    }
    private AttributedString makeString(String text, GraphicsBackend gfx){
        return gfx.makeAttributeString(text, new DankFont("helvetica", 0, 27));
    }

    public void setArrowState(boolean state){
        this.drawArrow = state;
    }
    public void enableSplitMode(){
        this.split_mode = true;
    }
    // move the arrow around using these methods
    public void selectRightOption(){
        this.right_opt = true;
    }
    public void selectBottomRow(){
        this.bottom_row = true;
    }
    public void forceEmpty(){
        this.forceempty = true;
    }

    @Override
    public void drawObject(GraphicsBackend gfx) {
        this.content = this.makeString(this.content_raw, gfx);
        if (this.split_mode){
            this.drawSplitMode(gfx);
        } else {
            this.drawRegularMode(gfx);
        }
    }

    // break the code out to have easier to manage chunks
    // non-split mode
    private void drawRegularMode(GraphicsBackend gfx){
        // set color for outer box
        // draw a rectangle that is the whole width but only 200px hight
        gfx.drawRectangleFilled(0, 400, 500, 100, DankColor.pink);
        // draw another one
        gfx.drawRectangleFilled(8, 408, 484, 84, DankColor.lightGray);
        /* HOTFIX: sometimes this needs to be empty
            so i added a new flag to allow it to be empty if need be
         */
        if (!this.forceempty) {
            // draw whatever text we have
            // set the color to black
            this.drawLineBrokenText(gfx, 480);
            //gfx.drawString(this.text, 10, 333);
            // check if we need to draw an arrow or not
            if (this.drawArrow) {
                gfx.drawPolygonFilled(new int[]{470, 490, 480}, new int[]{470, 470, 490}, 3, DankColor.green);
            }
        }
    }
    // split mode
    private void drawSplitMode(GraphicsBackend gfx){
        // here, we need to draw two boxes.
        // but we can cheat and draw one long box first
        gfx.drawRectangleFilled(0, 400, 500, 100, DankColor.pink);
        // next, draw the smaller one on the right side
        gfx.drawRectangleFilled(308, 408, 184, 84, DankColor.lightGray);
        // draw larger rectangle
        gfx.drawRectangleFilled(8, 408, 284, 84, DankColor.lightGray);
        // draw the text to the screen
        this.drawLineBrokenText(gfx, 180);
        // now we need to draw the options on the right side
        // color should still be black from drawLineBrokenText
        DankFont thefont = new DankFont("helvetica", 0, 23);
        // draw options
        // attack, magic, item, run
        gfx.drawTextSimple(330, 433, thefont, DankColor.black, "Attack");
        gfx.drawTextSimple(420, 433, thefont, DankColor.black, "Magic");
        gfx.drawTextSimple(330, 473, thefont, DankColor.black, "Item");
        gfx.drawTextSimple(420, 473, thefont, DankColor.black, "Run");
        // now, we need to draw the selection arrow
        // to do that, we need to do stupid maths
        int x = 0;
        int y = 0;
        if (this.right_opt){
            x += 90;
        }
        if (this.bottom_row){
            y += 40;
        }
        // change color
        // draw arrow
        gfx.drawPolygonFilled(new int[]{315 + x, 315 + x, 330 + x}, new int[]{410 + y, 436 + y, 423 + y}, 3, DankColor.green);
    }

    // draw stupid cringe text
    private void drawLineBrokenText(GraphicsBackend gfx, int width){
        gfx.setColor(DankColor.black);
        // setup the linebreaker thing
        AttributedCharacterIterator shit = this.content.getIterator();
        // we want to break on 480 pixel bounds (2 pixels from the edge of each side
        int breakwidth = width;
        // base y pos
        float ypos = 433;
        // xpos for simplicity
        int xpos = 10;
        gfx.drawTextAdvanced(xpos, ypos, shit, breakwidth);
    }
}
