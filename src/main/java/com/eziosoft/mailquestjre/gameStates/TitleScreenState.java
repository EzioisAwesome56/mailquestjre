package com.eziosoft.mailquestjre.gameStates;

import com.eziosoft.mailquestjre.Main;
import com.eziosoft.mailquestjre.renderObjects.DrawableObject;
import com.eziosoft.mailquestjre.renderObjects.SimpleImageRenderer;
import com.eziosoft.mailquestjre.renderObjects.TitleScreenOptionsRenderer;
import com.eziosoft.mailquestjre.stuff.MousePos;
import com.eziosoft.mailquestjre.stuff.Player;
import com.eziosoft.mailquestjre.stuff.SaveFileUtils;
import com.eziosoft.mailquestjre.stuff.TextEntryPrompt;
import com.eziosoft.mailquestjre.stuff.enums.GameStates;

import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class TitleScreenState implements GameState {

    // state variables to manage the state of the title screen
    private int menu_item = 0;
    private boolean has_save = false;
    private int framecounter = 0;

    // stuff for caching loaded resources
    private BufferedImage logoimg;

    @Override
    public void preformState(ArrayList<DrawableObject> renderlist, ArrayList<Integer> keys, MousePos mouse) {
        if (this.logoimg == null) this.loadLogo();
        // draw the logo to the screen
        SimpleImageRenderer logo = new SimpleImageRenderer(this.logoimg, 0, -40);
        renderlist.add(logo);
        // draw the options to the screen
        TitleScreenOptionsRenderer options = new TitleScreenOptionsRenderer(this.has_save, this.menu_item);
        renderlist.add(options);
        // handle user input
        if (this.framecounter == 0) {
            if (keys.contains(KeyEvent.VK_DOWN)) {
                // add 1 to selected menu item
                this.menu_item += 1;
                // put 10 frames on the counter
                this.framecounter = 10;
            } else if (keys.contains(KeyEvent.VK_UP)){
                // subtract 1
                this.menu_item -= 1;
                // put 10 frames on the counter
                this.framecounter = 10;
            } else if (keys.contains(KeyEvent.VK_Z)){
                // handle it
                this.doMenuSelection();
            }
            // bounds checking
            if (this.menu_item < 0) this.menu_item = 0;
            if (this.has_save){
                if (this.menu_item > 4) this.menu_item = 4;
            } else {
                if (this.menu_item > 3) this.menu_item = 3;
            }
        } else {
            // subtract a frame from the counter
            this.framecounter -= 1;
        }
    }

    // these will be called later by the main thread on boot
    public void has_save(){
        this.has_save = true;
    }

    // load the logo image
    private void loadLogo(){
        try {
            // get the input stream of the resource we need
            InputStream stream = TitleScreenState.class.getResourceAsStream("/title/logo.png");
            this.logoimg = ImageIO.read(stream);
            // close the stream
            stream.close();
        } catch (IOException e){
            // rethrow it
            throw new RuntimeException("Error while loading assets", e);
        }
    }

    // handle menu item selection
    private void doMenuSelection(){
        // stupid hack so i can be a lazy idiot
        int sel = this.menu_item;
        if (!this.has_save){
            // if there is not a save file, add 1 and then we can continue like normal
            sel += 1;
        }
        if (Main.debugging) System.err.println("Option Selected: " + sel);
        switch (sel){
            case 0:
                // continue
                // call the routine to load the save file
                SaveFileUtils.loadSaveFile();
                // switch states to the overworld state
                Main.current_state = GameStates.OVERWORLD.id;
                break;
            case 1:
                // new game
                this.promptForName();
                // get the cutscene state
                CutsceneState cutscene = (CutsceneState) Main.getState(GameStates.CUTSCENE.id);
                // reset it
                cutscene.resetState();
                // tell it to load the opening cutscene
                // TODO: actually make this cutscene at some point
                cutscene.loadCutscene("opening");
                // switch to the cutscene state
                Main.current_state = GameStates.CUTSCENE.id;
                break;
            case 2:
                // options
                break;
            case 3:
                // erase save file
                // make sure they actually have a save file to erase first
                if (SaveFileUtils.checkForSaveFile()){
                    doSaveFileDelete();
                }
                break;
            case 4:
                // credits
                break;
        }
    }

    // prompt the user to enter a name. do not let them thru unless it meets the requirements
    private void promptForName(){
        // setup the basic variables
        String name = "";
        boolean valid = false;
        // setup the text prompt
        TextEntryPrompt prompt = new TextEntryPrompt("Please Enter Name");
        // loop until we have valid input
        try {
            while (!valid) {
                name = prompt.doPrompt();
                if (name.length() > 10 || name.isEmpty()){
                    if (Main.debugging) System.err.println("Not a valid name!");
                } else {
                    // valid name
                    valid = true;
                }
            }
        } catch (Exception e){
            // how the fuck did this error?
            throw new RuntimeException("how the fuck did you break this?", e);
        }
        // get rid of it
        prompt.destroy();
        // create a new player
        Main.player = new Player(name);
    }

    // ensure the user wants to delete their save file
    private void doSaveFileDelete(){
        boolean confirmed = false;
        boolean clicked = false;
        // setup the text promptt
        TextEntryPrompt prompt = new TextEntryPrompt("Type \"delete my save\" to confirm this action!");
        try {
            while (!clicked){
                String dank = prompt.doPrompt();
                if (dank.equals("delete my save")){
                    clicked = true;
                    confirmed = true;
                } else {
                    // do not set confirmed if they did not type that exactly into the box
                    clicked = true;
                }
            }
        } catch (Exception e){
            throw new RuntimeException("Error while showing user text prompt", e);
        }
        // did they confirm this action?
        if (confirmed){
            // reset the flag
            this.has_save = false;
            // delete it
            SaveFileUtils.deleteSaveFile();
        }
    }
}
