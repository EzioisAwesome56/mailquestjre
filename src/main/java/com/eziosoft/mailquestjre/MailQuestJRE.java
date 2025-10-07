package com.eziosoft.mailquestjre;

import com.alysoft.dankengine.Main;
import com.alysoft.dankengine.gameStates.testState;
import com.alysoft.dankengine.utility.TextSlicer;
import com.eziosoft.mailquestjre.gameStates.*;
import com.eziosoft.mailquestjre.stuff.*;
import com.eziosoft.mailquestjre.stuff.enums.GameStates;
import com.google.gson.Gson;
import java.util.*;

public class MailQuestJRE {

    // dumb hack to make porting easier
    public static Gson gson = Main.gson;
    public static Random random = Main.random;
    public static boolean debugging = Main.debugging;

    // game runtime memory
    public static HashMap<String, Object> state_storage = new HashMap<>();
    public static Player player;

    // other things for the engine to use
    public static KeyItemFlavorText keyitemtext;

    public static void PreformEarlyInit(){
        Main.window_title = "MailQuest: Java Edition";
        // register strings
        TextSlicer.registerString("<player>", player.getName());
    }

    public static void PreformInitialLoading() throws Exception {
        // we moved a bunch of this crap down here so any loading
        // errors with them can be caught by our error handler instead of
        // just being thrown to main's error handler
        // setup some basic shit
        Main.addState(new OverworldState());
        Main.addState(new BattleState());
        // dumb stupid hack for later
        TitleScreenState state = new TitleScreenState();
        Main.addState(state);
        Main.addState(new ObjectSortMinigameState());
        Main.addState(new PlayerStatPageState());
        Main.addState(new ItemMenuState());
        Main.addState(new CutsceneState());
        // TODO: get rid of the test state
        Main.addState(new testState());
        // init predefs
        PredefinedFunctions.predef_init();
        keyitemtext = new KeyItemFlavorText();
        // check for the existence of a save file
        // TODO: handle using either user directories or the same folder as the main executable
        if (SaveFileUtils.checkForSaveFile()){
           // tell the title screen we have a save file
            state.has_save();
        }
        // change the current state of the engine
        Main.current_state = GameStates.TITLE.id;
    }


}