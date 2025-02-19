package com.eziosoft.mailquestjre.renderObjects;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Hashtable;

public class TextboxObject implements DrawableObject{
    // line break magic was borrowed from a very old oracle JDK example
    // see https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/2d/text/examples/LineBreakSample.java
    // and also https://docs.oracle.com/javase/tutorial/2d/text/drawmulstring.html

    private AttributedString content;
    private boolean drawArrow = false;
    // set for if you wish to draw the nameplate
    private boolean drawNameplate = false;
    private String name;

    public TextboxObject(String text){
        // give this to the attributed string
        this.content = this.makeString(text);
    }
    public TextboxObject(String text, String name){
        // assume they want the nameplate
        this.drawNameplate = true;
        this.name = name;
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

    // note: the textbox can fit 6 lines of text

    @Override
    public void drawObject(Graphics2D gfx) {
        // set color for outer box
        gfx.setColor(Color.pink);
        // draw a rectangle that is the whole width but only 200px hight
        gfx.fillRect(0, 300, 500, 200);
        // draw another one
        gfx.setColor(Color.lightGray);
        gfx.fillRect(8, 308, 484, 184);
        // do we need to draw the nameplate?
        if (this.drawNameplate){
            // set the colors
            gfx.setColor(Color.pink);
            gfx.fillRect(0, 250, 200, 50);
            // inner rectangle
            gfx.setColor(Color.LIGHT_GRAY);
            gfx.fillRect(8, 258, 184, 42);
            // set font and color
            gfx.setColor(Color.black);
            gfx.setFont(new Font("helvetica", Font.PLAIN, 30));
            // draw it
            gfx.drawString(this.name, 10, 285);
        }
        // draw whatever text we have
        // set the color to black
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
        int breakwidth = 480;
        // base y pos
        float ypos = 333;
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
        //gfx.drawString(this.text, 10, 333);
        // check if we need to draw an arrow or not
        if (this.drawArrow){
            gfx.setColor(Color.green);
            gfx.fillPolygon(new int[]{470, 490, 480}, new int[]{470, 470, 490}, 3);
        }
    }
}
