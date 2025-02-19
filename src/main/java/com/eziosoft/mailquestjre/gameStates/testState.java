package com.eziosoft.mailquestjre.gameStates;

import com.eziosoft.mailquestjre.renderObjects.*;
import com.eziosoft.mailquestjre.stuff.MousePos;
import com.eziosoft.mailquestjre.stuff.TextSlicer;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class testState implements GameState{

    // state stuff
    private exampleObj hi = new exampleObj();
    private TiledMovement player = new TiledMovement();
    private TestMousePoint mouseobj = new TestMousePoint();
    private boolean isTextbox = false;
    private TextSlicer slice;
    private boolean arrow = false;
    private int arrow_counter = 0;
    private boolean seentext = false;

    @Override
    public void preformState(ArrayList<DrawableObject> renderlist, ArrayList<Integer> keys, MousePos mouse) {
        // run the main game loop for this state
        // check to see if mouse is present
        if (mouse.isInPanel()){
            // TODO: debug code, throw this out or something
            //this.mouseobj.addPoint(mouse.getX(), mouse.getY());
        }
        //renderlist.add(this.mouseobj);
        // lock movement if a textbox is shown
        if (!this.isTextbox) {
            if (keys.contains(KeyEvent.VK_RIGHT)) {
                this.player.ApplyMoveRight();
            } else if (keys.contains(KeyEvent.VK_LEFT)) {
                this.player.ApplyMoveLeft();
            } else if (keys.contains(KeyEvent.VK_DOWN)){
                this.player.ApplyMoveDown();
            } else if (keys.contains(KeyEvent.VK_UP)){
                this.player.ApplyMoveUp();
            }
        }
        // test for coordinates
        if (this.player.getTilex() == 6 && !this.isTextbox && !this.seentext){
            this.slice = new TextSlicer("If you have a paragraph of styled text that you would like to fit within a specific width, you can use the LineBreakMeasurer class. This class enables styled text to be broken into lines so that they fit within a particular visual advance. Each line is returned as a TextLayout object, which represents unchangeable, styled character data. However, this class also enables access to layout information. The getAscent and getDescent methods of TextLayout return information about the font that is used to position the lines in the component. The text is stored as an AttributedCharacterIterator object so that the font and point size attributes can be stored with the text.");
            this.isTextbox = true;
        }
        if (isTextbox){
            TextboxObject obj = new TextboxObject(this.slice.getText(), "gamer");
            // check to see if we are done printing text
            if (this.slice.doneSlicing()) {
                // check to see if we should show the arrow or not
                if (this.arrow_counter == 10) {
                    this.arrow = !this.arrow;
                    this.arrow_counter = 0;
                } else {
                    this.arrow_counter++;
                }
                obj.setArrowState(this.arrow);
                // look for input to disable the textbox next frame
                if (keys.contains(KeyEvent.VK_Z)){
                    this.isTextbox = false;
                    this.seentext = true;
                }
            }
            renderlist.add(obj);
        }
        // add everything to the render list
        renderlist.add(this.hi);
        renderlist.add(this.player);
        BattleExclaim exclaim = new BattleExclaim(5, 5);
        renderlist.add(exclaim);
    }
}
