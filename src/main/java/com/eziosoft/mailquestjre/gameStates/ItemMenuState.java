package com.eziosoft.mailquestjre.gameStates;

import com.eziosoft.mailquestjre.Main;
import com.eziosoft.mailquestjre.renderObjects.DrawableObject;
import com.eziosoft.mailquestjre.renderObjects.ItemMenuListRenderer;
import com.eziosoft.mailquestjre.renderObjects.ItemMenuTabRenderer;
import com.eziosoft.mailquestjre.renderObjects.TextboxObject;
import com.eziosoft.mailquestjre.stuff.ItemMenuListItem;
import com.eziosoft.mailquestjre.stuff.MousePos;
import com.eziosoft.mailquestjre.stuff.TextSlicer;
import com.eziosoft.mailquestjre.stuff.enums.GameStates;
import com.eziosoft.mailquestjre.stuff.enums.PlayerWeapons;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class ItemMenuState implements GameState{
    /*
    tab ids:
    0 -> items
    1 -> weapons
    2 -> key items
     */
    private int current_tab = 0;
    private int framecounter = 6;
    private boolean draw_tab_menu = false;
    private GameStates return_state;
    private int selected_item = 0;
    private int max_item = 0;
    // stuff  for displaying textboxes
    private TextSlicer slicer;
    private boolean show_textbox = false;
    private boolean show_arrow = false;

    // CALL THIS BEFORE RUNNING MAIN
    public void setReturnState(GameStates state){
        this.return_state = state;
    }

    @Override
    public void preformState(ArrayList<DrawableObject> renderlist, ArrayList<Integer> keys, MousePos mouse) {
        // create new item tab render
        ItemMenuTabRenderer tabs = new ItemMenuTabRenderer(this.current_tab);
        // disable the filled arrow if a tab is selected
        if (this.draw_tab_menu){
            tabs.disableSolid();
            ItemMenuListRenderer list = this.createItemList();
            renderlist.add(list);
        }
        // add everything to the renderer
        renderlist.add(tabs);
        // do we need to display a textbox?
        if (this.show_textbox){
            // create a new textbox
            TextboxObject txt = new TextboxObject(this.slicer.getText());
            // do stuff for when the slicer is done
            if (this.slicer.doneSlicing()) {
                // check frame counter
                if (this.framecounter == 0) {
                    // flip arrow state
                    this.show_arrow = !this.show_arrow;
                    // add 10 frames to it
                    this.framecounter = 10;
                } else {
                    // subtract 1 frame from the counter
                    this.framecounter -= 1;
                }
                // update arrow state
                txt.setArrowState(this.show_arrow);
                // get rid of it if required
                if (keys.contains(KeyEvent.VK_Z)) {
                    // disable textbox flag
                    this.show_textbox = false;
                    // add like 6 frames to the counter
                    this.framecounter = 6;
                }
            }
            // add it to the render list
            renderlist.add(txt);
        } else {
            // to prevent too-fast movement, we block inputs behind a frame counter
            if (this.framecounter == 0) {
                // only parse left and right inputs if the tab's menu is not drawn
                if (!this.draw_tab_menu) {
                    if (keys.contains(KeyEvent.VK_RIGHT)) {
                        // move tab right
                        this.current_tab++;
                        // bounds checking
                        if (this.current_tab > 2) this.current_tab = 2;
                        // put 10 frames on the counter to help prevent instant movement
                        this.framecounter = 10;
                    } else if (keys.contains(KeyEvent.VK_LEFT)) {
                        // move tab selection left
                        this.current_tab -= 1;
                        // bounds checking
                        if (this.current_tab < 0) this.current_tab = 0;
                        // put some frames on the counter
                        this.framecounter = 10;
                    } else if (keys.contains(KeyEvent.VK_Z)) {
                        // switch to the menu mode
                        this.draw_tab_menu = true;
                        // toss up some frames
                        this.framecounter = 10;
                        // reset the bounds for convience
                        this.max_item = 0;
                        // also reset current item
                        this.selected_item = 0;
                    } else if (keys.contains(KeyEvent.VK_X)) {
                        if (this.return_state == null) {
                            throw new IllegalStateException("No return state was provided!");
                        }
                        // exit the menu
                        Main.current_state = this.return_state.id;
                        // put frames on the counter for later
                        this.framecounter = 6;
                    }
                } else {
                    // processing for when a tab menu is drawn
                    if (keys.contains(KeyEvent.VK_X)) {
                        // exit the menu and go back to tab mode
                        this.draw_tab_menu = false;
                        // put 10 frames on the counter
                        this.framecounter = 10;
                    } else if (keys.contains(KeyEvent.VK_DOWN)) {
                        // move selection down
                        this.selected_item += 1;
                        // put frames on the counter
                        this.framecounter = 10;
                        // bounds checking
                        if (this.selected_item > (this.max_item - 1)) this.selected_item = (this.max_item - 1);
                    } else if (keys.contains(KeyEvent.VK_UP)) {
                        // move selection up
                        this.selected_item -= 1;
                        // toss some frames up
                        this.framecounter = 10;
                        // bounds checking
                        if (this.selected_item < 0) this.selected_item = 0;
                    } else if (keys.contains(KeyEvent.VK_Z)) {
                        // figure out what we need to do and do it
                        this.handleTabMenuSelection();
                        // put some frames on the counter
                        this.framecounter = 10;
                    }
                }
            } else {
                // subtract 1 from the frame counter
                this.framecounter -= 1;
            }
        }
    }

    /**
     * generates an item list based on what tab is selected
     * this code will probably be big and ugly so i decided to move it here
     */
    private ItemMenuListRenderer createItemList(){
        // make blank object here so everything else can use it
        ItemMenuListRenderer thing;
        // what tab did we select?
        if (this.current_tab == 1){
            // weapons tab
            // make new list to hold weapons
            ArrayList<ItemMenuListItem> list = new ArrayList<>();
            // add all of our current weapons to it
            for (PlayerWeapons w : Main.player.getUnlocked_weapons()){
                // check if it is in use
                boolean inuse = false;
                if (Main.player.getWeapon() == w) inuse = true;
                list.add(new ItemMenuListItem(w, inuse));
            }
            // create a new renderer with this  array list
            thing = new ItemMenuListRenderer(list, this.selected_item);
            // update the bounds checking value if needed
            if (this.max_item != list.size()) this.max_item = list.size();
        } else if (this.current_tab == 0){
            // items tab
            /*
            eariler builds just made the item menu crash
            that isnt cool cuz it could result in loss of save data
            so instead we'll just give it something to display
            TODO: come back once there are actually items
             */
            ArrayList<ItemMenuListItem> list = new ArrayList<>();
            list.add(new ItemMenuListItem("This needs to be here or"));
            list.add(new ItemMenuListItem("else game will crash"));
            list.add(new ItemMenuListItem("Feature isn't done yet"));
            thing = new ItemMenuListRenderer(list, this.selected_item);
            // this should always be here regardless, or else the arrow will move strangely
            if (this.max_item != list.size()) this.max_item = list.size();
        } else if (this.current_tab == 2){
            // Key items tab
            // create new list
            ArrayList<ItemMenuListItem> list = new ArrayList<>();
            // check if player has any key items even
            if (Main.player.getKeyitems().isEmpty()){
                list.add(new ItemMenuListItem("You have no Key Items"));
            } else {
                // copy all key items to new list
                for (String s : Main.player.getKeyitems()){
                    list.add(new ItemMenuListItem(s));
                }
            }
            // make a new menu list renderer
            thing = new ItemMenuListRenderer(list, this.selected_item);
            // inform the menu of the max selection bounds
            // else the green selection arrow will act strangely
            if (this.max_item != list.size()) this.max_item = list.size();
        } else {
            throw new IllegalArgumentException("Invalid item tab selected!");
        }
        // return the completed item render
        return thing;
    }

    /**
     * handles pressing the main action key (Z) to use an item.
     * as an example, using it on the weapons screen will equip the selected weapon to the player
     */
    private void handleTabMenuSelection(){
        // what state are we in?
        if (this.current_tab == 1){
            // weapons tab
            // get selected weapon
            PlayerWeapons wep = Main.player.getUnlocked_weapons().get(this.selected_item);
            // is it not already equipped?
            if (Main.player.getWeapon() != wep){
                // change equipped weapon to what is selected
                Main.player.equipNewWeapon(this.selected_item);
            }
            // and we're done
        } else if (this.current_tab == 2){
            // get the flavor text
            // HOTFIX: not having any items causes a crash; fix that shit
            String tmp;
            try {
                tmp = Main.keyitemtext.getFlavorText(Main.player.getKeyitems().get(this.selected_item));
            } catch (IndexOutOfBoundsException e){
                // the exception doesnt actually matter, but we will ignore it anyway
                tmp = "That is not a valid item!";
            }
            // setup text slicer
            this.slicer = new TextSlicer(tmp);
            // enable textbox flag
            this.show_textbox = true;
            // make sure arrow is off
            this.show_arrow = false;
        } else if (this.current_tab == 0){
            // items menu
            // TODO: this should do something, but since items as a whole don't exist, it does not
        }
    }
}
