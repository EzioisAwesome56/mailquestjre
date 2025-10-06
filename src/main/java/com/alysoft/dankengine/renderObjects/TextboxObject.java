package com.alysoft.dankengine.renderObjects;

import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderer.DankColor;
import com.alysoft.dankengine.renderer.DankFont;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Hashtable;

public class TextboxObject implements DrawableObject{
    // line break magic was borrowed from a very old oracle JDK example
    // see https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/2d/text/examples/LineBreakSample.java
    // and also https://docs.oracle.com/javase/tutorial/2d/text/drawmulstring.html

    private AttributedString content;
    private String content_raw;
    private boolean drawArrow = false;
    // set for if you wish to draw the nameplate
    private boolean drawNameplate = false;
    private String name;

    public TextboxObject(String text){
        // give this to the attributed string
        this.content_raw = text;
    }
    public TextboxObject(String text, String name){
        // assume they want the nameplate
        this.drawNameplate = true;
        this.name = name;
        this.content_raw = text;

    }
    private AttributedString makeString(String text, GraphicsBackend gfx){
        return gfx.makeAttributeString(text, new DankFont("helvetica", 0, 27));
    }

    public void setArrowState(boolean state){
        this.drawArrow = state;
    }

    // note: the textbox can fit 6 lines of text

    @Override
    public void drawObject(GraphicsBackend gfx) {
        // quickly, we need to get our content
        this.content = this.makeString(this.content_raw, gfx);
        // set color for outer box
        // draw a rectangle that is the whole width but only 200px hight
        gfx.drawRectangleFilled(0, 300, 500, 200, DankColor.pink);
        // draw another one
        gfx.drawRectangleFilled(8, 308, 484, 184, DankColor.lightGray);
        // do we need to draw the nameplate?
        if (this.drawNameplate){
            // set the colors
            gfx.drawRectangleFilled(0, 250, 200, 50, DankColor.pink);
            // inner rectangle
            gfx.drawRectangleFilled(8, 258, 184, 42, DankColor.lightGray);
            // set font and color
            DankFont font = new DankFont("helvetica", 0, 30);
            // draw it
            gfx.drawTextSimple(10, 285, font, DankColor.black, this.name);
        }
        // draw whatever text we have
        // set the color to black
        gfx.setColor(DankColor.black);
        // setup the linebreaker thing
        AttributedCharacterIterator shit = this.content.getIterator();
        // we want to break on 480 pixel bounds (2 pixels from the edge of each side
        int breakwidth = 480;
        // base y pos
        float ypos = 333;
        // xpos for simplicity
        int xpos = 10;
        gfx.drawTextAdvanced(xpos, ypos, shit, breakwidth);
        //gfx.drawString(this.text, 10, 333);
        // check if we need to draw an arrow or not
        if (this.drawArrow){
            gfx.drawPolygonFilled(new int[]{470, 490, 480}, new int[]{470, 470, 490}, 3, DankColor.green);
        }
    }
}
