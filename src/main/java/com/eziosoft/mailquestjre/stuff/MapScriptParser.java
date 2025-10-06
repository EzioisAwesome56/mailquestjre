package com.eziosoft.mailquestjre.stuff;

import com.alysoft.dankengine.Main;
import com.eziosoft.mailquestjre.MailQuestJRE;
import com.eziosoft.mailquestjre.gameStates.BattleState;
import com.eziosoft.mailquestjre.gameStates.OverworldState;
import com.eziosoft.mailquestjre.stuff.enums.GameStates;
import com.eziosoft.mailquestjre.stuff.enums.PlayerWeapons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapScriptParser {

    // stuff used by the actual parser
    private List<String> script;
    private int cur_line;
    private boolean script_cond;

    public MapScriptParser(String scriptname){
        /*
        i call this: the dumbass-finder-inator9000
        because i keep forgetting how to use my own code in scripts
        - DO NOT INCLUDE ANY PATH ELEMENTS
        - DO NOT INCLUDE A FILE EXTENSION
         */
        if (scriptname.contains(".des") || scriptname.contains("/")){
            throw new IllegalArgumentException("Script name contains invalid characters!");
        }
        // init everything to basics
        this.script_cond = false;
        this.cur_line = 0;
        // attempt to read in the script from resources
        String temp;
        try {
            temp = Main.getFunctionalBackend().getEngineTextResource("/overworld/scripts/" + scriptname + ".des");
        } catch (IOException e){
            // oh no
            throw new RuntimeException("Error trying to read in script resource", e);
        }
        // split by line breaks
        // from https://stackoverflow.com/a/454913
        String[] lines = temp.split("\\r?\\n");
        // convert to list
        this.script = new ArrayList<String>();
        this.script.addAll(Arrays.asList(lines));
        // and now we're done
    }

    // run loaded script
    public void runScript(OverworldState state){
        // loop thru all the strings to parse lines
        for (String line : this.script){
            if (line.startsWith("#")){
                // this is a comment; skip the line
                continue;
            }
            // preform actions here
            if (line.startsWith("TEXTBOX")){
                // display a textbox when script ends
                ReflectionUtils.displayTextbox(line.replace("TEXTBOX ", ""), state);
            } else if (line.startsWith("CHECK_FLAG")){
                // split string
                String[] split = line.split("\\s+");
                // check to see if flag exists
                if (MailQuestJRE.state_storage.containsKey(split[1])){
                    // check if false
                    if (!(boolean) MailQuestJRE.state_storage.get(split[1])){
                        // bail out due to failed condition
                        return;
                    }
                } else {
                    // this flag does not exist at all. probably normal buuuut
                    if (MailQuestJRE.debugging) Main.getFunctionalBackend().logError("Flag \"" + split[1] +"\" does not exit in state storage!");
                    // this returned originally but like, why? if the flag doesnt exist we shouldnt do this
                    // ^^ an idiot wrote this, getting rid of return breaks all previously-made collision updating scripts
                    //      so yes, this return is important!
                    return;
                }
            } else if (line.startsWith("MOD_TILEMAP")){
                // split by space again
                String[] split = line.split("\\s+");
                // parse what we need into ints
                int x = Integer.parseInt(split[1]);
                int y = Integer.parseInt(split[2]);
                int tile = Integer.parseInt(split[3]);
                // update the tilemap
                state.UpdateTileMap(x, y, tile);
            } else if (line.startsWith("DEL_EVENT")){
                // split line
                String[] split = line.split("\\s+");
                // execute the method that will handle all of this for us
                ReflectionUtils.deleteEventWithFlag(split[1], state);
            } else if (line.startsWith("SET_FLAG")){
                // split the line
                String[] split = line.split("\\s+");
                // set the flag in the state array to be true
                if (MailQuestJRE.state_storage.containsKey(split[1])){
                    // remove it
                    MailQuestJRE.state_storage.remove(split[1]);
                }
                // insert a new key into the table and set it to be true
                MailQuestJRE.state_storage.put(split[1], true);
            } else if (line.startsWith("RUN_SCRIPT")){
                // split the lines
                String[] split = line.split("\\s+");
                // make a new script parser
                if (MailQuestJRE.debugging) Main.getFunctionalBackend().logError("Script called script: " + split[1]);
                MapScriptParser subparser = new MapScriptParser(split[1]);
                subparser.runScript(state);
            } else if (line.startsWith("GIVE_WEAPON")){
                // split the line
                String[] split = line.split("\\s+");
                // get the weapon based on arg 1
                PlayerWeapons weapon;
                try {
                    weapon = PlayerWeapons.values()[Integer.parseInt(split[1])];
                } catch (Exception e){
                    throw new IllegalArgumentException("Invalid weapon id given in script!", e);
                }
                // give it to the player
                if (!MailQuestJRE.player.getUnlocked_weapons().contains(weapon)){
                    MailQuestJRE.player.getUnlocked_weapons().add(weapon);
                } else {
                    if (MailQuestJRE.debugging) Main.getFunctionalBackend().logError("Player already has " + weapon);
                }
            } else if (line.startsWith("ENCOUNTER")){
                // split line
                String[] split = line.split("\\s+");
                // cause an encounter to occur when returned to overworld
                ReflectionUtils.queueBattleOnOverworldReturn(split[1], Integer.parseInt(split[2]));
                // for debug reasons; force the menu closed
                if (MailQuestJRE.debugging) ReflectionUtils.forceMenuClosed();
            } else if (line.startsWith("MOD_FIGHTTYPE")){
                // split it and then get the new battle type
                String[] split = line.split("\\s+");
                int type = Integer.parseInt(split[1]);
                // update the type of battle to load
                BattleState battle = (BattleState) Main.getState(GameStates.BATTLE.id);
                battle.setBattleType(type);
            } else if (line.startsWith("FLAG_ON_WIN")){
                // split string
                String[] split = line.split("\\s+");
                // get the battle state
                BattleState battle = (BattleState) Main.getState(GameStates.BATTLE.id);
                // tell it the flag to set
                battle.setFlagOnBattleWin(split[1]);
            } else if (line.startsWith("START_BATTLE")){
                /*
                this is a combination of all the previous dumb hacks rolled into one function
                just it does not cause a queued battle in the overworld
                parameters in order:
                foe name
                level
                battle type
                flag to se on battle win
                 */
                String[] split = line.split("\\s+");
                // what we need to int
                int level = Integer.parseInt(split[2]);
                int type = Integer.parseInt(split[3]);
                // get the battle state
                BattleState battle = (BattleState) Main.getState(GameStates.BATTLE.id);
                // reset it
                battle.resetState();
                // load in our information
                battle.setFlagOnBattleWin(split[4]);
                battle.loadFoe(split[1]);
                battle.scaleFoeByLevel(level);
                battle.setBattleType(type);
                // switch scenes to battle
                Main.current_state = GameStates.BATTLE.id;
            } else if (line.startsWith("GIVE_KEYITEM")){
                // split string
                String[] split = line.split("\\s+");
                // add key item to our player's inventory
                MailQuestJRE.player.getKeyitems().add(split[1]);
            } else if (line.startsWith("LOAD_MAP")){
                // syntax: LOAD_MAP <mapname> <spawnpoint>
                // split the arguments
                String[] split = line.split("\\s+");
                // convert the second one to an int
                int spawn = Integer.parseInt(split[2]);
                // cause a map load
                state.setMapToLoad(split[1]);
                state.selectSpawnPoint(spawn);
                // for good measure
                state.forceMapReload();
            } else if (line.startsWith("SET_STATE")){
                // i thought we wouldnt need this but
                // the cutscene engine is poorly programmed so
                // assumes the script will set the state at the end of it
                // split args
                String[] split = line.split("\\s+");
                // convert to int
                int stateno = Integer.parseInt(split[1]);
                // switch to that state
                Main.current_state = stateno;
            }
        }
    }
}
