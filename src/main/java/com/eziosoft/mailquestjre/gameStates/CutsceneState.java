package com.eziosoft.mailquestjre.gameStates;

import com.alysoft.dankengine.Main;
import com.alysoft.dankengine.gameStates.GameState;
import com.alysoft.dankengine.renderObjects.DrawableObject;
import com.alysoft.dankengine.renderObjects.TextboxObject;
import com.alysoft.dankengine.renderer.DankGraphic;
import com.alysoft.dankengine.utility.DankButtons;
import com.alysoft.dankengine.utility.MousePos;
import com.alysoft.dankengine.utility.TextSlicer;
import com.eziosoft.mailquestjre.MailQuestJRE;
import com.eziosoft.mailquestjre.json.CutsceneFrame;
import com.eziosoft.mailquestjre.json.CutsceneMetaFile;
import com.eziosoft.mailquestjre.renderObjects.SimpleImageRenderer;
import com.eziosoft.mailquestjre.stuff.MapScriptParser;
import com.eziosoft.mailquestjre.stuff.enums.GameStates;

import java.io.IOException;
import java.util.ArrayList;

public class CutsceneState implements GameState {
    // state variables
    private CutsceneMetaFile cutscene_file;
    private int current_frame = 0;
    // TODO: we might not even need this tbh
    private boolean show_textbox = false;
    private boolean show_nameplate = false;
    private int framecounter = 0;
    private boolean show_arrow = false;
    private TextSlicer slicer;
    private String nameplate_text;
    private boolean loadNextFrame = true;
    private DankGraphic loaded_background;
    private boolean exit_cutscene = false;

    // reset the cutscene engine to a known-good state
    public void resetState(){
        this.current_frame = 0;
        this.show_textbox = false;
        this.show_nameplate = false;
        this.framecounter = 0;
        this.show_arrow = false;
        this.loadNextFrame = true;
        this.exit_cutscene = false;
    }
    // use this to load a cutscene file
    public void loadCutscene(String name){
        CutsceneMetaFile meta;
        try {
            // get our resource
            String tmp = Main.getFunctionalBackend().getEngineTextResource("/cutscene/data/" + name + ".json");
            // make gson read it to what we want
            meta = MailQuestJRE.gson.fromJson(tmp, CutsceneMetaFile.class);
        } catch (IOException e){
            // something broke, throw it
            throw new RuntimeException("Something went wrong trying to load cutscene meta file", e);
        }
        // set this meta file in our object's state
        this.cutscene_file = meta;
    }

    // load the next frame of the cutscene
    private void loadNextFrame(){
        // get the frame to load
        CutsceneFrame frame = this.cutscene_file.getFrames().get(this.current_frame);
        // setup a text slicer with the new information
        this.slicer = new TextSlicer(frame.getTextbox_text());
        // reset arrow state
        this.show_arrow = false;
        // do we need to configure the nameplate?
        if (frame.isShow_nameplate()){
            // enable the nameplate
            this.show_nameplate = true;
            // set the text
            // bonus feature: if <player>, replace with player's name
            String tmp = frame.getDisplayName();
            tmp = tmp.replace("<player>", MailQuestJRE.player.getName());
            this.nameplate_text = tmp;
        }
        // do we even want to load the next frame?
        // if the img is set to "reuse", then don't even bother using a new frame
        if (!frame.getFrame_img().equals("reuse")) {
            // next, we have to try and load the graphics for the frame
            // construct the path
            // FIXME: during demo JAR testing it was discovered that using . as a path for the root of the cutscene folder doesnt work
            //          perhaps handle this better later so this code doesnt smell like shit
            //          im too lazy and angry at maven to go and properly fix this atm
            //          2-19-2025
            String path = "/cutscene/gfx";
            if (!this.cutscene_file.getSubdir().equals(".")){
                // do the code normally
                path += "/" + this.cutscene_file.getSubdir() + "/";
            } else {
                path += "/";
            }
            path += frame.getFrame_img() + ".png";
            try {
                this.loaded_background = Main.getFunctionalBackend().getEngineGraphicResource(path);
            } catch (IOException e) {
                throw new RuntimeException("Something broke while trying to load cutscene graphics", e);
            }
        }
        // once loaded, we can reset the variable to load the next frame
        this.loadNextFrame = false;
    }

    @Override
    public void preformState(ArrayList<DrawableObject> renderlist, ArrayList<Integer> keys, MousePos mouse) {
        // is a new frame queued?
        if (this.loadNextFrame){
            // load the next frame
            this.loadNextFrame();
        }
        // only draw frames if we're not supposed to exit the cutscene
        if (!this.exit_cutscene){
            // draw the background graphic to the screen
            SimpleImageRenderer bg = new SimpleImageRenderer(this.loaded_background, 0, 0);
            renderlist.add(bg);
            // create the textbox
            TextboxObject txt;
            if (this.show_nameplate){
                // create textbox with nameplate
                txt = new TextboxObject(this.slicer.getText(), this.nameplate_text);
            } else {
                // create textbox without nameplate
                txt = new TextboxObject(this.slicer.getText());
            }
            // add the textbox to the render list
            renderlist.add(txt);
            // are we done slicing?
            if (this.slicer.doneSlicing()){
                // now we need to blink the arrow
                if (this.framecounter == 10){
                    // flip state of the arrow
                    this.show_arrow = !this.show_arrow;
                    // reset frame counter
                    this.framecounter = 0;
                } else {
                    // add 1 to the frame counter
                    this.framecounter += 1;
                }
                // update the state of the textbox arrow
                txt.setArrowState(this.show_arrow);
                // check for input
                if  (keys.contains(DankButtons.INPUT_ACTION)){
                    // increment current frame
                    this.current_frame += 1;
                    // are we out of frames?
                    if (this.current_frame < this.cutscene_file.getFrames().size()){
                        // trigger a frame load
                        this.loadNextFrame = true;
                    } else {
                        // set the flag to exit the cutscene
                        this.exit_cutscene = true;
                    }
                }
            }
        } else {
            // check if there is a script to run after this cutscene
            if (this.cutscene_file.getScript() != null){
                // check  its not just  "null" as a string
                if (!this.cutscene_file.getScript().equals("null")){
                    // load the script
                    MapScriptParser parser = new MapScriptParser(this.cutscene_file.getScript());
                    parser.runScript((OverworldState) Main.getState(GameStates.OVERWORLD.id));
                } else {
                    Main.current_state = GameStates.OVERWORLD.id;
                }
            } else {
                Main.current_state = GameStates.OVERWORLD.id;
            }
        }
    }
}
