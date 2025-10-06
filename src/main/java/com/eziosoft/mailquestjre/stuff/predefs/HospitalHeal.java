package com.eziosoft.mailquestjre.stuff.predefs;

import com.alysoft.dankengine.Main;
import com.alysoft.dankengine.utility.TextSlicer;
import com.eziosoft.mailquestjre.MailQuestJRE;
import com.eziosoft.mailquestjre.gameStates.OverworldState;
import com.eziosoft.mailquestjre.stuff.PredefinedFunctions;
import com.eziosoft.mailquestjre.stuff.enums.GameStates;

import java.lang.reflect.Field;

public class HospitalHeal extends PredefinedFunctions {

    @Override
    public void doPredef() {
        if (MailQuestJRE.debugging) Main.logInfo("Now running healing predef function!");
        // get the overworld game state
        OverworldState state = (OverworldState) Main.getState(GameStates.OVERWORLD.id);
        // get the actual map that we need from the current map
        // its time for reflection nonsense!
        String cur_map;
        try {
            Field thefield = OverworldState.class.getDeclaredField("map_filename");
            // make it accessible
            thefield.setAccessible(true);
            cur_map = (String) thefield.get(state);
            // undo that
            thefield.setAccessible(false);
        } catch (Exception e){
            // rethrow it, who cares
            throw new RuntimeException(e);
        }
        // ok, now we have the map fillename, split it
        String[] split = cur_map.split("_");
        // update map to the filename after the _
        MailQuestJRE.player.set_lasthealmap(split[1]);
        // now we can actually heal the player
        MailQuestJRE.player.fully_heal();
        // ok, now we should display a textbox
        // too lazy to actually do it so we're going to use more reflection shit
        // yay for reflection
        try {
            Field lock = OverworldState.class.getDeclaredField("lockControls");
            Field slicer = OverworldState.class.getDeclaredField("slicer");
            Field textbox = OverworldState.class.getDeclaredField("isTextbox");
            // set them all accessible
            lock.setAccessible(true);
            slicer.setAccessible(true);
            textbox.setAccessible(true);
            // enable the control lock
            lock.set(state, true);
            lock.setAccessible(false);
            // set the text we want to display
            slicer.set(state, new TextSlicer("You have been healed. Try not to get so hurt next time!"));
            slicer.setAccessible(false);
            // enable the textbox flag
            textbox.set(state, true);
            textbox.setAccessible(false);
            // in theory, we should be done now
        } catch (Exception e){
            throw new RuntimeException("Oops, something broke :(", e);
        }
        // and now we're done :D
    }
}
