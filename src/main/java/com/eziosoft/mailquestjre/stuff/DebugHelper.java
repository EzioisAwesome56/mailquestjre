package com.eziosoft.mailquestjre.stuff;

import com.eziosoft.mailquestjre.Main;
import com.eziosoft.mailquestjre.gameStates.BattleState;
import com.eziosoft.mailquestjre.gameStates.CutsceneState;
import com.eziosoft.mailquestjre.gameStates.ObjectSortMinigameState;
import com.eziosoft.mailquestjre.gameStates.OverworldState;
import com.eziosoft.mailquestjre.stuff.enums.GameStates;
import org.apache.commons.lang3.SystemUtils;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class DebugHelper {
    /*
    in order to not pollute the main classes with debug code,
    this class was created to contain a bunch of methods that
    contain said code in place of the regular objects
    may or may not be removed later, who knows
     */
    private static boolean didbuff = false;

    public static void OverworldMenuDebug(ArrayList keys){
        if (keys.contains(KeyEvent.VK_NUMPAD8)) {
            // todo: this is a secret debugging thing
            //      to start a battle with some init code
            BattleState thestate = (BattleState) Main.getState(GameStates.BATTLE.id);
            thestate.resetState();
            thestate.loadFoe("debug");
            thestate.setWildEncounterText();
            // switch states
            Main.current_state = 1;
        } else if (keys.contains(KeyEvent.VK_NUMPAD7)){
            // disable all encounters for this area. good god this is annoying as hell
            ((OverworldState) Main.getState(GameStates.OVERWORLD.id)).setEncounterState(true);
        } else if (keys.contains(KeyEvent.VK_NUMPAD4)){
            // renable encounters
            ((OverworldState) Main.getState(GameStates.OVERWORLD.id)).setEncounterState(false);
        } else if (keys.contains(KeyEvent.VK_NUMPAD5)){
            // force close the menu
            ReflectionUtils.forceMenuClosed();
            // switch to sort minigame state
            ObjectSortMinigameState state = (ObjectSortMinigameState) Main.getState(GameStates.OBJSORT.id);
            // reset it
            state.resetState();
            // switch to it next frame
            Main.current_state = GameStates.OBJSORT.id;
        } else if (keys.contains(KeyEvent.VK_NUMPAD9)){
            // 9 is the debug key to start a cutscene!
            // load our cutscene engine
            CutsceneState state = (CutsceneState) Main.getState(GameStates.CUTSCENE.id);
            // reset it
            state.resetState();
            // load our cutscene
            state.loadCutscene("postman1");
            // switch scenes
            Main.current_state = GameStates.CUTSCENE.id;
        } else if (keys.contains(KeyEvent.VK_NUMPAD6)){
            if (!didbuff) {
                // buff the shit out of the player
                Main.player.addAtk(999);
                Main.player.addHP(999);
                Main.player.addDef(999);
                Main.player.fully_heal();
                System.err.println("Buffed the shit out of the player. Do not expect balance");
                didbuff = true;
            }
        } else if (keys.contains(KeyEvent.VK_NUMPAD1)){
            // prompt for map to load
            TextEntryPrompt prompt = new TextEntryPrompt("Enter map name to load");
            String map;
            try {
                map = prompt.doPrompt();
            } catch (InterruptedException e){
                // i dont know how the fuck this happened
                throw new IllegalStateException("How the FUCK did you cause this?", e);
            }
            // get the overworld state
            OverworldState ow = (OverworldState) Main.getState(GameStates.OVERWORLD.id);
            // pass in our map
            ow.setMapToLoad(map);
            // spawn at point 0 to make things easy
            ow.selectSpawnPoint(0);
        }
    }

    public static void loadMapDebug(){
        System.err.println("Force loading map route1...");
        // get the overworld state
        OverworldState ow = (OverworldState) Main.getState(GameStates.OVERWORLD.id);
        // set the variables as they should be
        ow.setMapToLoad("dampcave3");
        ow.selectSpawnPoint(0);
        // done for now
    }

    // i REALLY do not want to do this shit by hand so we will make an example
    public static void main(String[] args) throws Exception{
        /*// TODO: remove this at some point
        System.err.println("THIS IS NOT THE GAME, THIS IS GOD KNOWS WHAT DEBUGGING CODE");
        // get a new debug map tileset
        MapTileSet debug = new MapTileSet(2);
        // make  to string
        String why = Main.gson.toJson(debug);
        // write
        FileUtils.write(new File("bruh.json"), why);

         */
        System.err.println(SystemUtils.JAVA_VERSION);

    }
}
