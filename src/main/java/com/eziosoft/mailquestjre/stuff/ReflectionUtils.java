package com.eziosoft.mailquestjre.stuff;

import com.eziosoft.mailquestjre.Main;
import com.eziosoft.mailquestjre.gameStates.BattleState;
import com.eziosoft.mailquestjre.gameStates.OverworldState;
import com.eziosoft.mailquestjre.json.MapEvent;
import com.eziosoft.mailquestjre.json.OverworldMap;
import com.eziosoft.mailquestjre.stuff.enums.GameStates;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * I probably shouldnt even have this but like
 *
 * reflection is funny, what can i say?
 */
public class ReflectionUtils {

    // i am so tired of copy-pasting this code
    public static void displayTextbox(String text, OverworldState state){
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
            slicer.set(state, new TextSlicer(text));
            slicer.setAccessible(false);
            // enable the textbox flag
            textbox.set(state, true);
            textbox.setAccessible(false);
            // in theory, we should be done now
        } catch (Exception e){
            throw new RuntimeException("error while trying to activate textbox in relfection utils!", e);
        }
    }
    // i am lazy
    public static void displayTextbox(String text){
        OverworldState state = (OverworldState) Main.getState(GameStates.OVERWORLD.id);
        displayTextbox(text, state);
    }

    public static void deleteEventWithFlag(String flagname, OverworldState state){
        // first we need to get the actual eventlist
        List<MapEvent> events;
        try {
            Field curmap = OverworldState.class.getDeclaredField("current_map");
            // set accessible
            curmap.setAccessible(true);
            // try to get it
            OverworldMap map = (OverworldMap) curmap.get(state);
            // if we dont copy it, we get a concurrentmodificationexception which is very tasty
            events = new ArrayList<>(map.getEvents());
            // remove the event we want
            for (MapEvent event : events){
                // make sure the value isnt actually null, dumbass
                if (event.getFlagname() != null) {
                    if (event.getFlagname().equals(flagname)) {
                        // found it, deleting
                        int index = events.indexOf(event);
                        events.remove(index);
                        // done, yeet
                        break;
                    }
                }
            }
            // update the event list
            // we may have to do more reflection nonsense
            Field event_list = OverworldMap.class.getDeclaredField("events");
            event_list.setAccessible(true);
            event_list.set(map, events);
            // fix our shit
            event_list.setAccessible(false);
            // update it in the state too
            curmap.set(state, map);
            // undo the hackery
            curmap.setAccessible(false);
            // done
            if (Main.debugging) System.err.println("Deleted event with flag: " + flagname);
        } catch (Exception e){
            // oh dear lord
            throw new RuntimeException("Error while trying to delete event via reflection", e);
        }
    }

    public static void forceMenuClosed(){
        // get the field we want
        try {
            Field menu = OverworldState.class.getDeclaredField("isPauseMenu");
            // get the overworld state
            OverworldState state = (OverworldState) Main.getState(GameStates.OVERWORLD.id);
            // set our field to accessible
            menu.setAccessible(true);
            // set to false
            menu.set(state, false);
            // undo our work
            menu.setAccessible(false);
            // we're done here
        } catch (Exception e){
            throw new RuntimeException("Error while trying to update player's menu state", e);
        }

    }

    public static void queueBattleOnOverworldReturn(String foe, int level){
        // copy pasted from the first impl of it in MathChallengePredef
        // get the overworld state
        OverworldState overworld = (OverworldState) Main.getState(GameStates.OVERWORLD.id);
        // the battle state setup doesn't actually need reflection, but it's still here anyway for convience
        BattleState state = (BattleState) Main.getState(GameStates.BATTLE.id);
        state.resetState();
        state.loadFoe(foe);
        state.scaleFoeByLevel(level);
        // this will always be a wild battle
        state.setBattleType(0);
        // now we have to do the reflection shit to actually make the overworld state see a battle is queued
        // we can do this using more tasty reflection
        try {
            Field battlequeue = OverworldState.class.getDeclaredField("battle_queued");
            // set the value to true
            battlequeue.setAccessible(true);
            battlequeue.set(overworld, true);
            // undo that
            battlequeue.setAccessible(false);
        } catch (Exception e){
            throw new RuntimeException("Error during reflection for queuing battle", e);
        }
    }
}
