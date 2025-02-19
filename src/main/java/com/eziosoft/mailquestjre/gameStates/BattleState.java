package com.eziosoft.mailquestjre.gameStates;

import com.eziosoft.mailquestjre.renderObjects.*;
import com.eziosoft.mailquestjre.stuff.Player;
import com.eziosoft.mailquestjre.stuff.enums.GameStates;
import org.apache.commons.io.IOUtils;
import com.eziosoft.mailquestjre.Main;
import com.eziosoft.mailquestjre.entities.SimpleEntity;
import com.eziosoft.mailquestjre.json.FightableEntity;
import com.eziosoft.mailquestjre.entities.BattleEntity;
import com.eziosoft.mailquestjre.stuff.MousePos;
import com.eziosoft.mailquestjre.stuff.TextSlicer;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BattleState implements GameState {

    // state variables go here
    private BattleEntity foe;
    private BattleEntity player;
    // spaget for managing game state
    private boolean playerturn = true;
    private boolean foeturn = false;
    private int framecounter = 0;
    private boolean showfulltextbox = true;
    private TextSlicer slicer;
    private String text_to_display;
    private boolean textarrow = false;
    private boolean draw_statusbox = false;
    private boolean inittextdone = false;
    private boolean doDamage = false;
    private boolean missed = false;
    private int damage_to_deal;
    private int foe_status = 0;
    // TODO: player status effects
    private boolean foedead = false;
    private boolean playerdead = false;
    private boolean queuetextupdate = false;
    private boolean battleover = false;
    private boolean exit_to_overworld = false;
    private boolean crit = false;
    private boolean doLevelUp = false;
    private boolean doingLvlUp = false;
    private boolean queueLevelUp = false;
    private boolean stat_wheel_spin = false;
    // new feature mostly meant for scripting
    private boolean set_flag_on_win = false;
    private String flag_name;
    // stuff to make the menu work correctly
    private boolean action_cancelled = false;
    // 2-12-2025: any battle type besides 0 you should not be able to flee from
    // this flag will help fix it
    private boolean can_flee = true;
    /*
    states:
    0 -> nothing
    1 -> fail
    2 -> sucess
    3 -> "you cant flee!"
     */
    private int flee_state = 0;
    /*
    0 -> foe
    1 -> player
    2 -> somebody died
     */
    private int target;
    /*
    0 -> attack
    1 -> magic
    2 -> item
    3 -> run
     */
    private int item_selected = 0;
    // sprites for each one
    private BattleSprite player_sprite;
    private BattleSprite foe_sprite;

    // we need to feed the battlestate information when we start a battle
    public void resetState(){
        // reset frame counter
        this.framecounter = 0;
        this.textarrow = false;
        this.draw_statusbox = false;
        this.inittextdone = false;
        this.foe_status = 0;
        this.loadPlayer();
        this.playerturn = true;
        this.item_selected = 0;
        this.doDamage = false;
        this.missed = false;
        this.damage_to_deal = 0;
        this.target = 0;
        this.queuetextupdate = false;
        this.foeturn = false;
        this.playerdead = false;
        this.foedead = false;
        this.battleover = false;
        this.exit_to_overworld = false;
        this.showfulltextbox = true;
        this.flee_state = 0;
        this.crit = false;
        this.doLevelUp = false;
        this.doingLvlUp = false;
        this.queueLevelUp = false;
        this.stat_wheel_spin = false;
        this.set_flag_on_win = false;
        this.action_cancelled = false;
        this.can_flee = true;
        // TODO: this; probably clear out all the gunk if required
    }

    /**
     * @deprecated use loadFoe instead. Will probably be removed at some point in the future
     */
    @Deprecated // probably wont use this for anything serious
    public void provideFoe(BattleEntity foe, String spritename){
        this.foe = foe;
        this.foe_sprite = new BattleSprite("/battle/" + spritename + ".png", 300, 0);
    }
    public void loadFoe(String name){
        // attempt to load json data for what we want to fight
        String json;
        try {
            InputStream stream = BattleState.class.getResourceAsStream("/battle/data/" + name + ".json");
            json = IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (IOException e){
            System.err.println("Either the data you requested does not exist, or an error occured");
            throw new RuntimeException(e);
        }
        // then, read the object from the json data
        FightableEntity ent = Main.gson.fromJson(json, FightableEntity.class);
        // fill out the things we need
        // TODO: this may need a rework later
        //      if i end up making more entities
        this.foe = new SimpleEntity(ent);
        // load the foe's sprite
        this.foe_sprite = new BattleSprite("/battle/" + ent.getGraphic() + ".png", 300, 0);
    }
    // this will scale a foe's base states by whatever level you provided to the function in question.
    public void scaleFoeByLevel(int lvl){
        // if the level is one, we do not need to do scaling
        // otherwise the monster will be stronger then intended, which is uhhh not a good thing
        if (lvl == 1){
            return;
        }
        // we need to scale the foe's stats based on the level
        // since, as loaded, they are all level 1
        for  (int x = 1; x < lvl; x++){
            // increase level by 1
            this.foe.increaseLevel();
            // increase all states randomly
            this.foe.addAtk(Main.random.nextInt(3) + 1);
            this.foe.addDef(Main.random.nextInt(3) + 1);
            this.foe.addHP(Main.random.nextInt(5) + 1);
            // only add magic if the foe already has atleast 1 mp
            if (this.foe.getMaxMagic() > 0){
                this.foe.addMagic(Main.random.nextInt(2) + 1);
            }
        }
    }

    private void loadPlayer(){
        if (Main.player == null){
            System.err.println("You are trying to enter a battle without having a character!");
            System.err.println("That's illegal, bailing out");
            throw new RuntimeException("Attempted to start battle without character");
        }
        this.player = Main.player;
        this.player_sprite = new BattleSprite("/battle/player.png", 0, 200);
    }

    /**
     * @deprecated use setBattleType(0) instead
     */
    @Deprecated
    public void setWildEncounterText(){
        this.setBattleType(0);
    }
    /*
    there are several battle types
    0 -> wild
    1 -> regular npc
    2 -> boss fight
    anything else causes an error
     */
    public void setBattleType(int type){
        switch (type){
            case 0:
                this.text_to_display = "A wild " + this.foe.getName() + " appeared!";
                break;
            case 1:
                // TODO: regular npc fight text
            case 2:
                // boss test
                this.text_to_display = "Boss " + this.foe.getName() + " approaches you to kick ass!";
                // you cant flee from a boss
                this.can_flee = false;
                break;
            default:
                this.text_to_display = "An error battle has started!";
                break;
        }
        this.slicer = new TextSlicer(this.text_to_display);
    }

    /**
     * used in map scripts to enable a flag to be set if you win. good for disabling
     * events that trigger a boss fight
     * @param flag name of flag
     */
    public void setFlagOnBattleWin(String flag){
        this.set_flag_on_win = true;
        this.flag_name = flag;
    }


    @Override
    public void preformState(ArrayList<DrawableObject> renderlist, ArrayList<Integer> keys, MousePos mouse) {
        // draw both battle sprites
        if (!this.foedead){
            renderlist.add(this.foe_sprite);
        }
        if (!this.playerdead){
            renderlist.add(this.player_sprite);
        }
        BattleTextbox txt;
        if (this.doLevelUp){
            // level up code is handled elsewhere
            txt = this.doLevelUpState(renderlist, keys);
        } else if (this.showfulltextbox){
            // displaying the big textbox at the bottem; also responsible for exiting to overworld
            txt = this.showTextboxState(renderlist, keys);
        } else if (this.playerturn && !this.battleover){
            // player's turn state; you select an action here n shit
            txt = this.doPlayerTurnState(renderlist, keys);
        } else if (this.foeturn) {
            // moved to a function
            txt = this.doFoeTurnState(renderlist, keys);
        } else if (this.battleover) {
            // state when something happens to cause a battleover state
            txt = this.doBattleOverState(renderlist, keys);
        } else {
            // ran after foe's turn completes
            txt = this.doRoundEndState(renderlist, keys);
        }
        renderlist.add(txt);
        // check to see if the initial textbox is done
        // and its time to draw status boxes
        if (this.inittextdone && this.draw_statusbox) this.drawStatusbox(renderlist);
    }

    // decluttering main loop
    private void handleMenuInput(ArrayList keys){
        boolean inputed = false;
        if (keys.contains(KeyEvent.VK_RIGHT)){
            if (this.item_selected > 1){
                // this means its in the bottom row atm
                this.item_selected++;
                if (this.item_selected > 3) this.item_selected = 3;
            } else {
                // this means its on the top row
                this.item_selected++;
                if (this.item_selected > 1) this.item_selected = 1;
            }
            inputed = true;
        } else if (keys.contains(KeyEvent.VK_LEFT)){
            // pretty much a copy and paste of above with some tweaks
            if (this.item_selected > 1){
                // this means its in the bottom row atm
                this.item_selected--;
                if (this.item_selected < 2) this.item_selected = 2;
            } else {
                // this means its on the top row
                this.item_selected--;
                if (this.item_selected < 0) this.item_selected = 0;
            }
            inputed = true;
        } else if (keys.contains(KeyEvent.VK_DOWN)){
            // move the cursor down by adding 2
            this.item_selected += 2;
            if (this.item_selected > 3) this.item_selected = 3;
            inputed = true;
        } else if (keys.contains(KeyEvent.VK_UP)){
            // move the cursor up by subtracing 2
            this.item_selected -= 2;
            if (this.item_selected < 0) this.item_selected = 0;
            inputed = true;
        }
        // did we get an input?
        if (inputed){
            // add 6 to the frame counter
            this.framecounter = 6;
        }
    }

    /*
    the main method for this class is really big and annoying to work with, so I'm going to break
    the different if statement paths into their own functions for ease of keeping track of the giant if statement
     */
    private BattleTextbox showTextboxState(ArrayList<DrawableObject> renderlist, List keys){
        // hackfix: make txt
        BattleTextbox txt;
        // original code below
        if ((this.doDamage || this.missed || this.battleover) && this.queuetextupdate){
            this.displayBattleStatusText();
            this.slicer = new TextSlicer(this.text_to_display);
        }
        // if there is text to display, show it until the textbox is dismissed
        txt = new BattleTextbox(this.slicer.getText());
        // set arrow state
        txt.setArrowState(this.textarrow);
        if (this.slicer.doneSlicing()){
            // have 10 frames gone by?
            if (this.framecounter == 10){
                // update state
                this.textarrow = !this.textarrow;
                // reset counter
                this.framecounter = 0;
            } else {
                // increment counter
                this.framecounter += 1;
            }
            // check for input
            if (keys.contains(KeyEvent.VK_Z)){
                // clear the textbox
                this.showfulltextbox = false;
                // code for handling the initial textbox
                if (!this.inittextdone){
                    // if this is the init textbox, update variables
                    this.inittextdone = true;
                    this.draw_statusbox = true;
                    // add a couple frames to the counter
                    this.framecounter = 5;
                }
                // code for exiting once this textbox is cleared
                if (this.exit_to_overworld){
                    // TODO: either here or in where this flag is set,
                    //      if we need to teleport/load a new map, do so before changing game states
                    // TODO ALSO: detect if a level up is in order, and switch to the level up scene.
                    //      that doesnt exist yet but it needs to!
                    if (this.playerdead) this.doStuffForPlayerDeath();
                    Main.current_state = GameStates.OVERWORLD.id;
                }
                // switch states for a level up
                if (this.queueLevelUp){
                    // disable that flag
                    this.queueLevelUp = false;
                    // enable the real levelup flag
                    this.doLevelUp = true;
                }
            }
        }
        //  hackfix for moving this somewhere else
        // return our textbox
        return txt;
    }
    // runs to process a player's input for selecting their action
    private BattleTextbox doPlayerTurnState(ArrayList renderlist, ArrayList keys){
        // hackfix: define txt right at the start
        BattleTextbox txt;
        // original code below
        // if this variable is false, assume we want to show the split textbox for action selection
        txt = new BattleTextbox("What will " + this.player.getName() + " do?");
        // enable split mode
        txt.enableSplitMode();
        // do math
        int temp = this.item_selected - 2;
        if (temp >= 0){
            // they are on the bottom row
            txt.selectBottomRow();
        }
        if (Math.abs(temp) == 1){
            // right option selected
            txt.selectRightOption();
        }
        // do menu movement
        // debounce using framecounter
        if (this.framecounter > 0){
            this.framecounter--;
        } else {
            this.handleMenuInput(keys);
            // check to see if an option was selected
            if (keys.contains(KeyEvent.VK_Z)) {
                switch (this.item_selected) {
                    case 0: // Atack button
                        // attempt do a normal attack
                        int res = this.player.performRegularAttack(this.foe);
                        if (res == -1) {
                            // we did no damage, cuz we missed
                            this.missed = true;
                        } else {
                            // tell the game we did damage
                            this.doDamage = true;
                            // roll for a crit
                            int critroll = Main.random.nextInt(10);
                            if (critroll == 3){
                                // we got a crit
                                this.crit = true;
                                res = res * 2;
                            }
                            this.damage_to_deal = res;
                        }
                        // set target
                        this.target = 0;
                        break;
                    // TODO: finish the rest of the menu items
                    case 1: // magic
                        // TODO: this
                        //      for now, cancel the action so that nothing actually happens
                        this.action_cancelled = true;
                        break;
                    case 2: // items
                        // TODO: see above
                        //      this fixes using either of these menu items just causing a flee action
                        this.action_cancelled = true;
                        break;
                    case 3: // run
                        // can we even flee in the first place?
                        if (this.can_flee) {
                            // generate a random number
                            int num = Main.random.nextInt(17);
                            if (num <= 5) {
                                this.battleover = true;
                                this.exit_to_overworld = true;
                                this.flee_state = 2;
                            } else {
                                this.flee_state = 1;
                            }
                        } else {
                            this.flee_state = 3;
                        }
                        // dumb stupid hack to force this spaghetti code to print the text we want
                        this.missed = true;
                        this.target = 0;
                        break;
                    default:
                        System.err.println("How the fuck did you trigger this?");
                        throw new RuntimeException("Invalid menu item selected!");
                }
                if (!this.action_cancelled) {
                    // we have preformed an action. disable the ability for us to do anything
                    this.showfulltextbox = true;
                    this.playerturn = false;
                    this.foeturn = true;
                    // also signal we need to update the text
                    this.queuetextupdate = true;
                } else {
                    // make sure to reset action cancelled
                    this.action_cancelled = false;
                }
            }
        }
        // return txt to the calling state; this should make it work as if it was in the giant IF loop
        return txt;
    }
    // handle the foe's turn
    private BattleTextbox doFoeTurnState(ArrayList renderlist, ArrayList keys){
        // FIX: create a new battletextbox object to make this happy
        BattleTextbox txt;
        // original code below
        // this is run after displaying our action
        // apply damage if required
        if (this.doDamage && this.damage_to_deal > 0){
            // who do we apply it to?
            if (this.target == 0){
                // foe gets hurt
                this.foe.takeDamage(this.damage_to_deal);
            } else {
                throw new IllegalStateException("Player did damage but is targeted at player????");
            }
            // reset those variables
            this.doDamage = false;
            this.damage_to_deal = 0;
            this.crit = false;
        }
        // reset this variable here
        // it was in the block above, but that was causing
        // foe to miss when only player did
        this.missed = false;
            /* FIXME: HACKALERT!
                    i didn't think about allowing/disallowing foes to use magic, so we're gonna cheat a little bit
                    if the damage routine returns -2, then we will preform a magic attack
            */
        // HACK ALERT 2: foe can still attack if at negative HP, so uhh
        // fix that shit
        if (this.foe.getHealth() > 0) {
            int foe_res = this.foe.performRegularAttack(this.player);
            // also, set target to the player (aka target to 1)
            this.target = 1;
            if (foe_res == -2) {
                // TODO: preform magic attack
            } else if (foe_res == -1) {
                // missed
                this.missed = true;
            } else {
                // apply damage
                this.doDamage = true;
                int critroll = Main.random.nextInt(34);
                if (critroll ==  11){
                    // foe got a crit
                    this.crit = true;
                    if (Main.debugging) System.err.println("Foe landed crit, originally was: " + foe_res);
                    foe_res = foe_res * 2;
                }
                this.damage_to_deal = foe_res;
            }
            // once the foe's logic is done running, we need to flip some variables around
            this.queuetextupdate = true;
            this.showfulltextbox = true;
        }
        // this needs to run to skip foe's turn if they're also dead
        // not just in the block above!
        this.foeturn = false;
        // force an empty textbox
        txt = new BattleTextbox("you should not be able to read this");
        txt.forceEmpty();
        // return the txt object once we're done
        return txt;
    }
    // run after both turns are complete to clean up
    private BattleTextbox doRoundEndState(ArrayList renderlist, List keys){
        // hackfix: define new txt object
        BattleTextbox txt;
        // original code below
        // after foe's turn completes
        // see if we have to apply damage
        if (this.doDamage && this.damage_to_deal > 0){
            if (this.target == 1){
                // deal damage to the player
                this.player.takeDamage(this.damage_to_deal);
            } else {
                // this should not happen
                throw new IllegalStateException("After foe's turn yet somehow foe is taking damage????");
            }
        }
        /* FIX: these where in the if statement above
        however, that was causing them to not reset if the foe actually did miss and dealt no damage
        which meant the player can be reported to "miss" despite doing damage
        putting them below the if statement fixes this problem
         */
        // reset all the variables involved
        this.doDamage = false;
        this.damage_to_deal = 0;
        this.missed = false;
        // dont forget to reset the the crit variable
        this.crit = false;
        // TODO: status affects. foestatus already exists but we need it for player too
        // find out if anybody fucking died
        if (this.foe.getHealth() <= 0){
            // foe died
            this.target = 2;
            this.foedead = true;
        }
        if (this.player.getHealth() <= 0){
                /*
                in the unlikely event that the player and foe die on the same turn,
                we will spare the player by granting them 1 hp point
                i feel like if you manage to cause that, you deserve to not die :)
                 */
            if (this.foedead){
                this.player.heal(1);
            } else {
                // well, you died before foe did
                this.playerdead = true;
                this.target = 2;
            }
        }
        // preform textbox update if required
        if (this.target == 2){
            this.queuetextupdate = true;
            this.showfulltextbox = true;
            this.battleover = true;
        }
        // empty textbox to make the rest of this code happy
        txt = new BattleTextbox("DECOMPILATION <insert bird meme here>");
        txt.forceEmpty();
        // throw a couple of frames on the frame counter
        this.framecounter = 6;
        // finally, set the state to be the player's turn
        this.playerturn = true;
        // as this is now a method, return txt
        return txt;
    }
    // runs when this.battleover is true
    private BattleTextbox doBattleOverState(ArrayList<DrawableObject> renderlist, List keys){
        // dumb hackfix to make this code happy
        BattleTextbox txt;
        // original code below
        // check to see if the player fucking died
        if (this.playerdead){
            this.slicer = new TextSlicer("You lost the battle...");
        } else {
            // generate how much money and exp the player gets
            int exp = Main.random.nextInt(this.foe.getAtk()) + 1;
            int money = Main.random.nextInt(this.foe.getDef()) + 1;
            this.slicer = new TextSlicer("You won! You got "+ Integer.toString(exp) + " exp and " + Integer.toString(money) + " money");
            // grant the player the money and experience points
            Player pobj = (Player) this.player;
            pobj.gainMoney(money);
            pobj.gainExp(exp);
            // check to see if the player leveled up
            long needed = pobj.getNext_exp();
            long hadexp = pobj.getExp();
            if (hadexp > needed){
                // we should set this flag and deal with it in another area of the game
                this.queueLevelUp = true;
            }
            // should we set a flag?
            if (this.set_flag_on_win){
                // set the flag
                Main.state_storage.put(this.flag_name, true);
            }
        }
        this.showfulltextbox = true;
        txt = new BattleTextbox("Chat this is a massive W");
        txt.forceEmpty();
        // only exit to overworld if a levelup is not queued
        if (!this.queueLevelUp) this.exit_to_overworld = true;
        // return our textbox
        return txt;
    }

    // i don't know why i broke this out into its own function, but here we are I guess
    // i aint putting it back, the main routine is already so damn full of shit already
    private void drawStatusbox(ArrayList<DrawableObject> renderlist){
        // create new box
        if (!this.foedead) {
            BattleStatusBox foebox = new BattleStatusBox(this.foe, false, "");
            renderlist.add(foebox);
        }
        if (!this.playerdead) {
            BattleStatusBox playerbox = new BattleStatusBox(this.player, true, "");
            renderlist.add(playerbox);
        }
    }

    // TODO: account for magic usage. just displaying "attacked" is kinda lame lowkey
    // deal with figuring out what text to display
    private void displayBattleStatusText(){
        /* HOTFIX: the arrow sometimes gets stuck on the screen
            so we disable it whenever there is a call to update the text
         */
        this.textarrow = false;
        // who preformed this action?
        if (this.target == 0){
            // NEW: see if the player won a flee attempt
            if (this.flee_state == 0) {
                String base = this.player.getName() + " Attacked!";
                // player preformed this action
                if (this.missed) {
                    base += "        ...but missed!";
                } else {
                    base += "        dealt " + Integer.toString(this.damage_to_deal) + " damage!";
                    // append extra info if its a critical hit
                    if (this.crit){
                        base += " CRITICAL HIT!";
                    }
                }
                this.text_to_display = base;
            } else if (this.flee_state == 1){
                // failure state
                this.text_to_display = this.player.getName() + " tried to run away, but fell over and failed to do so...";
                // reset to be 0
                this.flee_state = 0;
            } else if (this.flee_state == 3){
                // why are you even trying to do this?
                this.text_to_display = this.player.getName() + " tried to run away, but was prevented from doing so by " + this.foe.getName();
                this.flee_state = 0;
            } else {
                // assume its 2; if its not two then we have a problem
                this.text_to_display =  this.player.getName() + " ran from the fight!";
            }
        } else if (this.target == 1){
            // foe did this
            String base = this.foe.getName() + " Atttacked!";
            if (this.missed){
                base += "      ...but missed!";
            } else {
                base += "       dealt " + Integer.toString(this.damage_to_deal) + "  damage!";
                // append more text if it is a critical hit
                if (this.crit){
                    base += " CRITICAL HIT!";
                }
            }
            this.text_to_display = base;
        } else if (this.target == 2){
            if (this.foedead){
                this.text_to_display = "Foe " + this.foe.getName() + " collapsed!";
            } else if (this.playerdead){
                this.text_to_display = this.player.getName() + " got too hurt and collapsed...";
            } else {
                this.text_to_display = "If you see this, please report to developer!";
            }
        }
        // disable the queue update flag
        this.queuetextupdate = false;
    }

    // move the player to the last place they healed if they died
    private void doStuffForPlayerDeath(){
        // first, change overworld state
        OverworldState state = (OverworldState) Main.getState(GameStates.OVERWORLD.id);
        // get the player's last heal map
        state.setMapToLoad(Main.player.get_lasthealmap());
        // set the spawn point
        // -1 is the key to use the heal spawn point
        state.selectSpawnPoint(-1);
        // force reload map
        state.forceMapReload();
        // heal the player
        Main.player.fully_heal();
    }

    // this is exclusively used for the level up
    // thats why its not listed above with the other object vars; it litterally doesnt matter
    private final int[] stat_adds = new int[4];

    /*
    this code is for doing the level up stuff. also does cool things like Mario and Luigi stat bonuses from
    a roulette wheel. very cool
     */
    private BattleTextbox doLevelUpState(ArrayList<DrawableObject> renderlist, List keys){
        // create variables for use later
        BattleTextbox txt = new BattleTextbox("why");
        txt.forceEmpty();
        Player pobj = (Player) this.player;
        // are we doing a level up right now?
        if (this.doingLvlUp){
            LevelUpStatDisplay stat = new LevelUpStatDisplay(pobj.getMaxHealth(), this.stat_adds[0], pobj.getAtk(), this.stat_adds[1], pobj.getDef(), this.stat_adds[2], pobj.getMagic(), this.stat_adds[3], this.item_selected);
            renderlist.add(stat);
            // some stuff for the random stat boost
            int rng = 0;
            if (this.stat_wheel_spin){
                // generate a random number
                rng = Main.random.nextInt(8) + 1;
                // draw that to the screen
                StatBoostWheel wheel = new StatBoostWheel(rng);
                renderlist.add(wheel);
            }
            // process user input to select stat to boost
            if (this.framecounter == 0) {
                if (!this.stat_wheel_spin) {
                    if (keys.contains(KeyEvent.VK_DOWN)) {
                        // move selected item down
                        this.item_selected += 1;
                        this.framecounter = 10;
                        // bounds checking
                        if (this.item_selected > 3) this.item_selected = 0;
                    } else if (keys.contains(KeyEvent.VK_UP)) {
                        // move selectted item up
                        this.item_selected -= 1;
                        this.framecounter = 10;
                        // bounds checking
                        if (this.item_selected < 0) this.item_selected = 3;
                    } else if (keys.contains(KeyEvent.VK_Z)){
                        // key pressed, switch to the wheel mode
                        this.stat_wheel_spin = true;
                        // add some frames
                        this.framecounter = 5;
                    }
                } else {
                    if (keys.contains(KeyEvent.VK_X)){
                        // cancel the wheel spin
                        this.stat_wheel_spin = false;
                        // add a couple of frames
                        this.framecounter = 5;
                    } else if (keys.contains(KeyEvent.VK_Z)){
                        String text = "got extra " + Integer.toString(rng) + " added to stat ";
                        // apply pre-generated stat boosts
                        pobj.addAtk(this.stat_adds[1]);
                        pobj.addHP(this.stat_adds[0]);
                        pobj.addDef(this.stat_adds[2]);
                        pobj.addMagic(this.stat_adds[3]);
                        // add the extra stat boost
                        switch (this.item_selected){
                            case 0: // HP
                                pobj.addHP(rng);
                                text += "HP!";
                                break;
                            case 1: // ATK
                                pobj.addAtk(rng);
                                text += "ATK!";
                                break;
                            case 2: // DEF
                                pobj.addDef(rng);
                                text += "DEF!";
                                break;
                            case 3: // MAGIC
                                pobj.addMagic(rng);
                                text += "MP!";
                                break;
                        }
                        // increase player level
                        pobj.increaseLevel();
                        // setup a textbox
                        this.slicer = new TextSlicer(text);
                        // reset involved variables
                        this.doLevelUp = false;
                        this.doingLvlUp = false;
                        this.stat_wheel_spin = false;
                        // incase we are due for more level ups at once
                        this.queueLevelUp = true;
                        // display textbox
                        this.showfulltextbox = true;

                    }
                }
            } else {
                this.framecounter -= 1;
            }
        } else {
            // check if we need to do a level up in the first place
            if (pobj.getExp() >= pobj.getNext_exp()){
                // we are! set the flag
                this.doingLvlUp = true;
                // take away experience points
                long hadexp = pobj.getExp();
                hadexp -= pobj.getNext_exp();
                // reset player's experience points
                pobj.resetEXP();
                // give them whats leftover
                pobj.gainExp(hadexp);
                // increase required exp for next level
                pobj.increaseEXPRequirements();
                // roll 4 stat increases
                // HP
                this.stat_adds[0] = Main.random.nextInt(10) + 2;
                // ATK
                this.stat_adds[1] = Main.random.nextInt(4) + 1;
                // DEF
                this.stat_adds[2] = Main.random.nextInt(4) + 1;
                // Magic
                this.stat_adds[3] = Main.random.nextInt(5) + 1;
                // set the text to be level up text
                this.slicer = new TextSlicer(pobj.getName() + " leveled up!");
                // flip some flags around to show the full textbox, but not boot us
                this.doLevelUp = false;
                this.queueLevelUp = true;
                this.showfulltextbox = true;
            } else {
                // we don't need this displayed, so yeet
                this.doLevelUp = false;
                // because this is called when we are done, set the exit flag to true
                this.exit_to_overworld = true;
                this.showfulltextbox = true;
                this.slicer = new TextSlicer(pobj.getName() + " now needs " + Long.toString(pobj.getNext_exp()) + " EXP points for next level up!");
            }
        }
        return txt;
    }
}
