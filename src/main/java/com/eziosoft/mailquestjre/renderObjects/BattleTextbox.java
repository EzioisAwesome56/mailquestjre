package com.eziosoft.mailquestjre.renderObjects;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Hashtable;

public class BattleTextbox implements DrawableObject{
    // line break magic was borrowed from a very old oracle JDK example
    // see https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/2d/text/examples/LineBreakSample.java
    // and also https://docs.oracle.com/javase/tutorial/2d/text/drawmulstring.html

    private AttributedString content;
    private boolean drawArrow = false;
    private boolean split_mode = false;
    // option selection things
    private boolean right_opt = false;
    private boolean bottom_row = false;
    private boolean forceempty = false;

    public BattleTextbox(String text){
        // give this to the attributed string
        this.content = this.makeString(text);
    }
    private AttributedString makeString(String text){
        // we apparently need a hashmap for some of this shit
        Hashtable<TextAttribute, Object> map = new Hashtable<>();
        map.put(TextAttribute.FONT, new Font("helvetica", Font.PLAIN, 27));
        return new AttributedString(text, map);
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
    public void drawObject(Graphics2D gfx) {
        if (this.split_mode){
            this.drawSplitMode(gfx);
        } else {
            this.drawRegularMode(gfx);
        }
    }

    // break the code out to have easier to manage chunks
    // non-split mode
    private void drawRegularMode(Graphics2D gfx){
        // set color for outer box
        gfx.setColor(Color.pink);
        // draw a rectangle that is the whole width but only 200px hight
        gfx.fillRect(0, 400, 500, 100);
        // draw another one
        gfx.setColor(Color.lightGray);
        gfx.fillRect(8, 408, 484, 84);
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
                gfx.setColor(Color.green);
                gfx.fillPolygon(new int[]{470, 490, 480}, new int[]{470, 470, 490}, 3);
            }
        }
    }
    // split mode
    private void drawSplitMode(Graphics2D gfx){
        // here, we need to draw two boxes.
        // but we can cheat and draw one long box first
        gfx.setColor(Color.pink);
        gfx.fillRect(0, 400, 500, 100);
        // next, draw the smaller one on the right side
        gfx.setColor(Color.LIGHT_GRAY);
        gfx.fillRect(308, 408, 184, 84);
        // draw larger rectangle
        gfx.fillRect(8, 408, 284, 84);
        // draw the text to the screen
        this.drawLineBrokenText(gfx, 180);
        // now we need to draw the options on the right side
        // color should still be black from drawLineBrokenText
        gfx.setFont(new Font("helvetica", Font.PLAIN, 23));
        // draw options
        // attack, magic, item, run
        gfx.drawString("Attack", 330, 433);
        gfx.drawString("Magic", 420, 433);
        gfx.drawString("Item", 330, 473);
        gfx.drawString("Run", 420, 473);
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
        gfx.setColor(Color.green);
        // draw arrow
        gfx.fillPolygon(new int[]{315 + x, 315 + x, 330 + x}, new int[]{410 + y, 436 + y, 423 + y}, 3);
    }

    // draw stupid cringe text
    private void drawLineBrokenText(Graphics2D gfx, int width){
        gfx.setColor(Color.black);
        // setup the linebreaker thing
        AttributedCharacterIterator shit = this.content.getIterator();
        int textstart = shit.getBeginIndex();
        int textend = shit.getEndIndex();
        FontRenderContext frc = gfx.getFontRenderContext();
        LineBreakMeasurer measurer = new LineBreakMeasurer(shit, frc);
        // set the measurer to the start
        measurer.setPosition(textstart);
        // we want to break on 480 pixel bounds (2 pixels from the edge of each side
        int breakwidth = width;
        // base y pos
        float ypos = 433;
        // xpos for simplicity
        int xpos = 10;
        // draw loop
        while (measurer.getPosition() < textend){
            TextLayout layout = measurer.nextLayout(breakwidth);
            // draw it
            layout.draw(gfx, xpos, ypos);
            // add to the y coord
            // the oracle example did not work so this is a thug'd solution
            ypos += (float) layout.getBounds().getHeight();
        }
    }
}
