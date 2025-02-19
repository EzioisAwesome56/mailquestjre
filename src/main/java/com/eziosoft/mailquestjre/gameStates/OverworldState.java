package com.eziosoft.mailquestjre.gameStates;

import com.eziosoft.mailquestjre.Main;
import com.eziosoft.mailquestjre.json.*;
import com.eziosoft.mailquestjre.renderObjects.*;
import com.eziosoft.mailquestjre.stuff.*;
import com.eziosoft.mailquestjre.stuff.enums.EventTypes;
import com.eziosoft.mailquestjre.stuff.enums.GameStates;
import com.eziosoft.mailquestjre.stuff.enums.MovementDirections;
import com.eziosoft.mailquestjre.stuff.enums.WarpActivations;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

public class OverworldState implements GameState{

    // define objects that will always be present no matter what
    private final TiledMovement playergfx = new TiledMovement("/overworld/gfx/player_ow.png");
    private TextSlicer slicer;

    // stuff for the loaded map goes here
    private String map_filename;
    private OverworldMap current_map;
    private BufferedImage cached_map_bg;
    private SimpleImageRenderer maprenderer;
    // default to 0
    private int selected_spawn = 0;
    private MapTileSet tileset;
    // this also will be used for collision purposes
    // access via y, x
    private int[][] tile_map;
    private boolean loadFinished = false;
    private boolean forceRedraw = false;

    // state variables
    private boolean isTextbox = false;
    private boolean lockControls = false;
    private boolean isPauseMenu = false;
    private boolean hasmoved = false;
    private int menu_item_selected = 0;
    private int frame_counter = 0;
    private boolean battle_queued = false;
    private boolean show_arrow = false;
    private boolean event_over = false;
    private boolean has_warped = false;

    // debug state shit
    // TODO: maybe remove later?
    private boolean debug_disableencounters = false;

    // static variables
    private final static int menu_movement_delay = 6;

    @Override
    public void preformState(ArrayList<DrawableObject> renderlist, ArrayList<Integer> keys, MousePos mouse) {
        // do we need to redraw the map?
        if (this.forceRedraw){
            // set this to false
            this.forceRedraw = false;
            // prefirm a map redraw
            this.drawTilesToMap();
            // replace the map renderer
            this.maprenderer = new SimpleImageRenderer(this.cached_map_bg);
        }
        // do we need to do an inital load?
        if (!this.loadFinished){
            this.doInitialLoad();
        } else {
            // if the load is finished, then we can draw the map to the screen
            renderlist.add(this.maprenderer);
        }
        // delay to make sure you can escape the textbox
        if (this.event_over){
            if (this.frame_counter == 0){
                // unlock controls
                this.lockControls = false;
                // turn off the flag
                this.event_over = false;
            } else {
                // subtract 1 from the frame counter
                this.frame_counter -= 1;
            }
        }
        // is a battle queued?
        if (this.battle_queued && !this.isTextbox){
            // theres a couple different things we have to do here, controlled by the frame counter
            if (this.frame_counter > 0){
                // make sure controls are locked
                this.lockControls = true;
                // create new exclaim drawable
                BattleExclaim ex = new BattleExclaim(this.playergfx.getTilex(), this.playergfx.getTiley() - 1);
                renderlist.add(ex);
                this.frame_counter -= 1;
            } else {
                // if the framecounter is now 0, we need to do a couple of things
                // unqueue the battle
                this.battle_queued = false;
                // switch scenes
                Main.current_state = GameStates.BATTLE.id;
                /*
                HOTFIX: if you are standing on an event tile after returning from a battle,
                sometimes there can be a leftover action key press in the queue
                so instead of unlocking controls here, we will use the same delay used by events
                with like 10 frames on the counter
                 */
                this.event_over = true;
                this.frame_counter = 15;
            }
        }
        // check every warp to see if we should do additional logic
        if (!this.lockControls) {
            for (MapWarp warp : this.current_map.getWarps()) {
                if (this.playergfx.getTilex() == warp.getTilex() && this.playergfx.getTiley() == warp.getTiley()){
                    // might have found something. run additional warp logic
                    this.handleMapWarp(keys, warp);
                }
            }
        }
        // are we warping?
        if (this.has_warped){
            // set to false
            this.has_warped = false;
            // return; we need to skip the rest of this code
            return;
        }
        // should we attempt to do an encounter?
        if (this.current_map.getEncountermode() > 0){
            // check to see if this is ok to do
            if ((!this.lockControls) && (!this.isPauseMenu) && this.hasmoved && (!this.playergfx.isMoving())){
                // disable the has_moved variable
                this.hasmoved = false;
                // sure, lets try one out i suppose
                // generate a random number
                int rand = Main.random.nextInt(100);
                if (rand < this.current_map.getWildChance()){
                    // new feature for debugging because oh my god
                    if (!this.debug_disableencounters) {
                        if (Main.debugging) System.err.println("Battle triggered");
                        // run the code to setup a battle
                        this.rollWildEncounter();
                    }
                }
            }
        }
        // if controls arent locked, allow the player to move
        if (!this.lockControls && !this.playergfx.isMoving()){
            if (keys.contains(KeyEvent.VK_RIGHT)) {
                this.doMovementCollision(MovementDirections.RIGHT);
            } else if (keys.contains(KeyEvent.VK_LEFT)) {
                this.doMovementCollision(MovementDirections.LEFT);
            } else if (keys.contains(KeyEvent.VK_DOWN)){
                this.doMovementCollision(MovementDirections.DOWN);
            } else if (keys.contains(KeyEvent.VK_UP)){
                this.doMovementCollision(MovementDirections.UP);
            } else if (keys.contains(KeyEvent.VK_ENTER)){
                // lock controls
                this.lockControls = true;
                // set the menu flag
                this.isPauseMenu = true;
            }
        }
        // add plater to gfx list
        renderlist.add(this.playergfx);
        // is the menu open?
        if (this.isPauseMenu){
            // run the routine to handle that
            this.doPauseMenu(renderlist, keys, mouse);
        }
        // should we display a textbox?
        if (this.isTextbox){
            // textboxes are handled elsewhere
            this.displayTextbox(keys, renderlist);
        }
        // should we process events?
        if (!this.lockControls && !this.isTextbox && !this.isPauseMenu && !this.playergfx.isMoving()){
            // go thru all the events
            for (MapEvent event : this.current_map.getEvents()){
                // is the player standing on the right tile for this event?
                if ((event.getX() == this.playergfx.getTilex()) && (event.getY() == this.playergfx.getTiley())){
                    // yes! time to do stuff
                    this.mapEventActivation(keys, event);
                }
            }
        }
    }

    /*
    this actually loads the map when called
    gets automatically called if loadFinished is set to false
    make sure you have a valid filename set first though!
     */
    private void doInitialLoad(){
        // first, we have to try and load whatever json file it wants us to load
        OverworldMap map;
        try {
            InputStream stream = OverworldState.class.getResourceAsStream("/overworld/data/maps/" + this.map_filename + ".json");
            // convert to string
            String raw = IOUtils.toString(stream, StandardCharsets.UTF_8);
            // convert to object
            map = Main.gson.fromJson(raw, OverworldMap.class);
            // close the stream
            stream.close();
        } catch (Exception e){
            // something broke
            throw new IllegalStateException("Error while trying to read map \""+ this.map_filename + "\"'s data", e);
        }
        // ok, we have the map now, set it into place
        this.current_map = map;
        // we will now attempt to load the tileset for this map now
        MapTileSet tileset;
        try {
            // try to open the file
            InputStream stream = OverworldState.class.getResourceAsStream("/overworld/data/tileset/" + this.current_map.getTileset() + ".json");
            // convert to string
            String raw = IOUtils.toString(stream, StandardCharsets.UTF_8);
            // close stream
            stream.close();
            // make json convert to object
            tileset = Main.gson.fromJson(raw, MapTileSet.class);
        } catch (Exception e){
            // just rethrow it
            throw new IllegalStateException("Error while trying to read tileset \"" + this.current_map.getTileset() + "\"'s data!", e);
        }
        // store the tileset
        this.tileset = tileset;
        // the final thing we need is the actual map data, which we will read from TMX
        try {
            this.tile_map = TiledMapUtils.loadTileMap(this.current_map.getTilemap());
        } catch (Exception e){
            // something broke, just throw a runtime exception
            throw new IllegalStateException("Error while trying to load block data for map " + this.map_filename, e);
        }
        // ok, we have now got the tilemap
        // now we are in a state to run the map's loadScript if it exists
        if (this.current_map.getLoad_script() != null) {
            if (!Objects.equals(this.current_map.getLoad_script(), "null")) {
                // attempt to run the script
                MapScriptParser parser = new MapScriptParser(this.current_map.getLoad_script());
                parser.runScript(this);
            }
        }
        // now we just have to draw it
        this.drawTilesToMap();
        // setup the actual map renderer object too
        this.maprenderer = new SimpleImageRenderer(this.cached_map_bg);
        // one final thing
        this.spawnPlayer();
        // once done, set the loading flag
        this.loadFinished = true;
    }
    // stuff for setting up map loading/changing
    public void setMapToLoad(String filename){
        this.map_filename = filename;
        this.loadFinished = false;
    }
    public void forceMapReload(){
        this.loadFinished = false;
    }
    // because other maps/scripts can control where the player spawns, we need to be able to set it
    public void selectSpawnPoint(int spawn){
        this.selected_spawn = spawn;
    }
    // i am too lazy to do more reflection shit for this
    public void UpdateTileMap(int x, int y, int new_tile){
        this.tile_map[y][x] = new_tile;
        // cause a map redraw
        this.forceRedraw = true;
    }
    // this is only used while trying to load a saved game
    // we need it to set the player's coordinates on the map
    public void setPlayerCoords(int x, int y){
        this.playergfx.setTileCoords(x, y);
        // DIRTY HACKFIX: even tho this is supposed to work, it doesnt
        // because the map loader always uses a spawnpoint
        // we will cheat and set selected_spawn to -2
        // to indicate that we have set coords manually
        this.selected_spawn = -2;
    }

    // this handles the player opening the pause menu
    // who wouldve guessed?
    private void doPauseMenu(ArrayList<DrawableObject> renderlist, ArrayList<Integer> keys, MousePos mouse){
        // make new menu
        PlayerMenu menu = new PlayerMenu(this.menu_item_selected);
        // add this to the renderlist
        renderlist.add(menu);
        // input time: check to see if the menu has been closed
        // then do movement shit
        // but first check to see if there are frames in the counter
        if (this.frame_counter == 0) {
            if (keys.contains(KeyEvent.VK_X)) {
                // disable the menu flag
                this.isPauseMenu = false;
                // and unlock player controls
                this.lockControls = false;
            } else if (keys.contains(KeyEvent.VK_DOWN)) {
                this.menu_item_selected += 1;
                this.frame_counter = menu_movement_delay;
                // bounds checking
                if (this.menu_item_selected > 4) this.menu_item_selected = 4;
            } else if (keys.contains(KeyEvent.VK_UP)) {
                this.menu_item_selected -= 1;
                this.frame_counter = menu_movement_delay;
                // more bounds checking
                if (this.menu_item_selected < 0) this.menu_item_selected = 0;
            } else if (keys.contains(KeyEvent.VK_Z)){
                // do something based on what menu item is selected
                switch (this.menu_item_selected){
                    case 0: // stats button
                        // switch states
                        Main.current_state = GameStates.STATS.id;
                        break;
                    case 1: // item menu
                        // switch state to the item menu
                        Main.current_state = GameStates.ITEMS.id;
                        // tell it the return state
                        ((ItemMenuState) Main.getState(GameStates.ITEMS.id)).setReturnState(GameStates.OVERWORLD);
                        break;
                    case 2: // TODO: MAP
                    case 3: // save button
                        // TODO: we should probably prompt to see if the user actually wants to save
                        //      but that is scope creep to deal with later
                        // call the function to save the game
                        SaveFileUtils.CreateandWriteSaveFile(this.map_filename, this.playergfx.getTilex(), this.playergfx.getTiley());
                        break;
                    case 4: // TODO: quit
                }
                // add some frames to the counter
                this.frame_counter = 10;
            }
            // TODO: stuff and things for actually making the menu items work
            if (Main.debugging) DebugHelper.OverworldMenuDebug(keys);
        } else {
            // subtract from the frame counter
            this.frame_counter--;
        }
    }

    /*
    trying to keep the main function managable, so im breaking a lot of stuff out into their own functions
    this is to handle displaying a textbox previously setup by something else
     */
    private void displayTextbox(List keys, ArrayList<DrawableObject> renderlist){
        // create a new textbox
        TextboxObject txt = new TextboxObject(this.slicer.getText());
        // is the slicer done slicing?
        if (this.slicer.doneSlicing()){
            // check to see if the frame counter is empty
            if (this.frame_counter == 0){
                // flip the state of the textbox arrow
                this.show_arrow = !this.show_arrow;
                // put 10 frames on the counter
                this.frame_counter = 10;
            } else {
                // subtract 1 frame from the counter
                this.frame_counter -= 1;
            }
            // check to see if z is pressed
            if (keys.contains(KeyEvent.VK_Z)){
                // disable the textbox
                this.isTextbox = false;
                // add 6 frames to the framecounter
                // check to see if a battle is queued or not
                if (!this.battle_queued) {
                    this.frame_counter = 6;
                    // set event_over
                    this.event_over = true;
                } else {
                    // ONLY put frames on the counter
                    this.frame_counter = 100;
                }
                // disable the arrow
                this.show_arrow = false;
            }
        }
        // set the arrow flag in the textbox object
        txt.setArrowState(this.show_arrow);
        // append to render list
        renderlist.add(txt);
    }

    /*
    this method will draw the map onto the cached map background image
    provided you have managed to properly load the tilemap from the XML files in quest
    if not, then why the hell are you here?
     */
    private void drawTilesToMap(){
        System.out.println("Drawing overworld map...this may take a little bit");
        if (this.cached_map_bg != null){
            this.cached_map_bg.flush();
        }
        // to ease on read calls, we will setup a cache of buffered images to use
        HashMap<Integer, BufferedImage> graphicscache = new HashMap<>();
        // setup other variables to use
        int xpos = 0;
        int ypos = 0;
        // create the image to draw onto
        this.cached_map_bg = new BufferedImage(500, 500, BufferedImage.TYPE_4BYTE_ABGR);
        // then, get the graphics from that
        Graphics2D gfx = this.cached_map_bg.createGraphics();
        // time to do the things
        for (int y = 0; y < 20; y++){
            for (int x = 0; x < 20; x++){
                // get the current tile in the tilemap matrix
                int tile = this.tile_map[y][x];
                // load the graphics if need be
                BufferedImage todraw;
                if (graphicscache.containsKey(tile)){
                    todraw = graphicscache.get(tile);
                } else {
                    // ok, we have to load it, damn
                    // get the stream of the resource in question
                    if (Main.debugging) System.out.println("Current Tile ID: " + tile);
                    String resourcename = "/overworld/gfx/" + this.tileset.getBasefolder() + "/" + this.tileset.getTiles().get(tile).getGraphics() + ".png";
                    InputStream stream = OverworldState.class.getResourceAsStream(resourcename);
                    // attempt to load it
                    try {
                        todraw = ImageIO.read(stream);
                        // close the stream
                        stream.close();
                    } catch (IOException e){
                        // something broke, just give up
                        throw new IllegalStateException("Error trying to load tile graphic " + this.tileset.getTiles().get(tile).getGraphics(), e);
                    }
                    // next, cache it for later
                    graphicscache.put(tile, todraw);
                    if (Main.debugging) System.err.println("Loaded tile graphics: " + this.tileset.getTiles().get(tile).getGraphics() + ".png");
                }
                // now, we can draw the tile onto the map
                gfx.drawImage(todraw, xpos, ypos, null);
                // add 25 to our xpos
                xpos += 25;
            }
            // we end of this x loop, reset xpos to 0
            xpos = 0;
            // add 25 to ypos
            ypos += 25;
        }
        // we're done drawing now, time to clean up
        gfx.dispose();
        // get rid of all the buffered images
        for (Map.Entry<Integer, BufferedImage> ent : graphicscache.entrySet()){
            ent.getValue().flush();
        }
        // delete the list
        graphicscache.clear();
        // and now we're done! yay
    }


    // deal with spawning the player in the right place
    private void spawnPlayer(){
        SpawnPoint point;
        // if its -1, use the heal spawn
        if (this.selected_spawn == -1){
            point = this.current_map.getSpawns().get(this.current_map.getHeal_spawn());
        } else if (this.selected_spawn == -2){
            // this means we set the spawn manually
            // yeet out of this functions
            return;
        }else {
            // otherwise, use normal logic
            point = this.current_map.getSpawns().get(this.selected_spawn);
        }
        // update the coords of the player graphic thing
        this.playergfx.setTileCoords(point.getX(), point.getY());
    }

    /**
     * do collision detection for a movement. will also send the movement stuff to the playergfx object
     * @param dir which direction?
     */
    private void doMovementCollision(MovementDirections dir){
        // first, get current tile x+y directions
        int tilex = this.playergfx.getTilex();
        int tiley = this.playergfx.getTiley();
        // add whatever number is required to it now
        if (dir.equals(MovementDirections.RIGHT)){
            tilex += 1;
        } else if (dir.equals(MovementDirections.LEFT)){
            tilex -= 1;
        } else if (dir.equals(MovementDirections.UP)){
            tiley -= 1;
        } else if (dir.equals(MovementDirections.DOWN)){
            tiley += 1;
        }
        // then, get the new tile from the collision map
        int tile = this.tile_map[tiley][tilex];
        // test if walkablle
        if (this.tileset.getTiles().get(tile).isWalkable()){
            // if yes, apply movement
            if (dir.equals(MovementDirections.RIGHT)){
                this.playergfx.ApplyMoveRight();
            } else if (dir.equals(MovementDirections.LEFT)){
                this.playergfx.ApplyMoveLeft();
            } else if (dir.equals(MovementDirections.UP)){
                this.playergfx.ApplyMoveUp();
            } else if (dir.equals(MovementDirections.DOWN)){
                this.playergfx.ApplyMoveDown();
            }
            // set hasmoved to true for something later
            this.hasmoved = true;
        }
    }

    // roll for a wild encounter
    // may also be used to setup the battle itself
    private void rollWildEncounter(){
        // get the size of the encounter table
        int size = this.current_map.getEncounters().size();
        // generate a number based on that
        int rand = Main.random.nextInt(size);
        // get the table entry
        EncounterTableEntry ent = this.current_map.getEncounters().get(rand);
        // get the battle state
        BattleState state = (BattleState) Main.getState(GameStates.BATTLE.id);
        // reset it
        state.resetState();
        // telll it to load a foe
        state.loadFoe(ent.getId());
        // scale the foe by level
        state.scaleFoeByLevel(ent.getLevel());
        // set wild encounter text
        state.setBattleType(0);
        // we're pretty much done, queue a battle and throw some frames on the counter
        this.battle_queued = true;
        this.frame_counter = 100;
        /*
        HOTFIX ON A HOTFIX:
        turns out with the original code, if you kept the movement keys held down, you could never get an encounter
        i fixed that, but then it allowed funny bugs with moving with the exclaim above your head
        so i guess we have to also lock controls here, too
         */
        this.lockControls = true;
    }

    // event parser, used to read event attributes and figure out what we need to do
    private void mapEventActivation(List keys, MapEvent event){
        // what type of event is it?
        int act = event.getActivation_type();
        if (act == 0){
            // if its 0, just run it asap
            this.preformEvent(event);
        } else if (act == 1){
            // check to see if the z button has been pressed or not
            if (keys.contains(KeyEvent.VK_Z)){
                // ok, the button has been pressed. now we need to do shit
                this.preformEvent(event);
            } else {
                // otherwise, fail out
                return;
            }
        }
    }
    // code to actually preform the event in question
    private void preformEvent(MapEvent event){
        /*
        2-9-2025 new FEATURE: enable or disable events based on flags
         */
        // get flag name
        String fname = event.getFlagname();
        // is this event disabled by a flag?
        if (event.getFlaguse() == 0){
            // check and see if the flag is present in the state storage
            if (Main.state_storage.containsKey(fname)){
                // check to make sure it is set to true
                if ((boolean) Main.state_storage.get(fname)){
                    // this event's flag is present and is true, bail
                    return;
                }
            }
        } else if (event.getFlaguse() == 1) {
            // this event is ENABLED by a flag
            // do the inverse of above
            if (Main.state_storage.containsKey(fname)) {
                // check to make sure it is set to true
                if (!(boolean) Main.state_storage.get(fname)) {
                    // this event's flag is present and is false, bail
                    return;
                }
            } else {
                // the flag doesnt exist, so bail
                return;
            }
        }
        // now back to regular event processing code
        int type = event.getEvent_type();
        if (type == EventTypes.TEXT.id){
            // lock controls
            this.lockControls = true;
            // setup textslicer
            this.slicer = new TextSlicer(event.getEventtext());
            // enable textbox flag
            this.isTextbox = true;
            // bail out
            return;
        } else if (type == EventTypes.PREDEF.id){
            // convert the provided id to an int
            int id = Integer.parseInt(event.getEventtext());
            // run the predef function
            PredefinedFunctions.runPredef(id);
            if (Main.debugging) System.out.println("Returned from predef call");
        } else if (type == EventTypes.SCRIPT.id){
            // run the script provided
            MapScriptParser parser = new MapScriptParser(event.getEventtext());
            parser.runScript(this);
        } else if (type == EventTypes.CUTSCENE.id){
            // get the cutscene scene
            CutsceneState scene = (CutsceneState) Main.getState(GameStates.CUTSCENE.id);
            // reset it
            scene.resetState();
            // tell it to load our script
            scene.loadCutscene(event.getEventtext());
            // switch scenes
            Main.current_state = GameStates.CUTSCENE.id;
        }
    }

    /*
    this code will run if the game detects we are standing on a warp tile
    and will handle doing all the shit warps require to function correctly
     */
    private void handleMapWarp(List keys, MapWarp warp){
        // check to see what activation type it is
        if (warp.getActivation_type() == WarpActivations.RIGHT.id){
            // did the player press right?
            if (keys.contains(KeyEvent.VK_RIGHT)){
                // perform the warp
                this.doWarp(warp);
            }
        } else if (warp.getActivation_type() == WarpActivations.LEFT.id){
            // is left pressed?
            if (keys.contains(KeyEvent.VK_LEFT)){
                this.doWarp(warp);
            }
        } else if (warp.getActivation_type() == WarpActivations.UP.id){
            if (keys.contains(KeyEvent.VK_UP)){
                this.doWarp(warp);
            }
        } else if (warp.getActivation_type() == WarpActivations.DOWN.id){
            if (keys.contains(KeyEvent.VK_DOWN)){
                this.doWarp(warp);
            }
        } else if (warp.getActivation_type() == WarpActivations.BUTTON.id){
            // check to see if the player has pushed the action button while standing on a warp
            if (keys.contains(KeyEvent.VK_Z)){
                // do the warp
                this.doWarp(warp);
            }
        }
    }
    // actually doing the warping
    private void doWarp(MapWarp warp){
        // set the flag to true
        this.has_warped = true;
        // prepare the map for a load
        this.setMapToLoad(warp.getDestination());
        this.selectSpawnPoint(warp.getSpawnpoint());
        // and now we're done, more or less
    }

    /**
     * @deprecated DO NOT USE FOR PRODUCTION; set "encountermode" to 0 in your map's json file to disable encounters for that area!
     */
    // spooky debug function that shouldnt exist but oh my god i am going insane
    @Deprecated
    public void setEncounterState(boolean state){
        if (Main.debugging){
            // toggle that shit
            this.debug_disableencounters = state;
            System.err.println("Encounters are now set to: " + this.debug_disableencounters);
        }
    }
}
