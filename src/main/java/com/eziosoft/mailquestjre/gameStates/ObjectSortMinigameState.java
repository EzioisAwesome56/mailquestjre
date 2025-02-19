package com.eziosoft.mailquestjre.gameStates;

import com.eziosoft.mailquestjre.Main;
import com.eziosoft.mailquestjre.renderObjects.*;
import com.eziosoft.mailquestjre.stuff.MousePos;
import com.eziosoft.mailquestjre.stuff.ReflectionUtils;
import com.eziosoft.mailquestjre.stuff.TextSlicer;
import com.eziosoft.mailquestjre.stuff.enums.GameStates;
import com.eziosoft.mailquestjre.stuff.enums.MovementDirections;
import org.apache.commons.lang3.time.StopWatch;

import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ObjectSortMinigameState implements GameState{
    // static variables to control things
    private static final long total_time = 60;
    private static final int required_score = 100;

    /*
    to win this state, you need to get a score of atleast TDB TODO: THAT
    in about 120 seconds or so. If you make one mistake, you fail and  have to start over. damn, what a scam
     */
    // state variables
    private boolean failed = false;
    private StopWatch watch = new StopWatch();
    private int score_left = 0;
    private int score_right = 0;
    private long timer;
    private boolean game_started = false;
    private TextSlicer slicer;
    private int framecounter = 0;
    private boolean textbox_arrow = false;
    private SortObject obj;
    private boolean obj_on_screen = false;
    private boolean moving = false;
    private MovementDirections dir;
    private BufferedImage background;
    /*
    0 -> red
    1 -> blue
     */
    private int type = 0;

    public void resetState(){
        // reset everything to default
        this.failed = false;
        this.watch.reset();
        this.score_left = 0;
        this.score_right = 0;
        this.timer = 1;
        this.game_started = false;
        this.framecounter = 0;
        this.slicer = null;
        this.textbox_arrow = false;
        this.obj_on_screen = false;
        this.moving = false;
        this.dir = MovementDirections.LEFT;
        // load the image from resources
        try {
            InputStream stream = ObjectSortMinigameState.class.getResourceAsStream("/title/sort.png");
            this.background = ImageIO.read(stream);
            // close the stream
            stream.close();
        } catch (IOException e){
            throw new RuntimeException("Error while trying to load sort background image", e);
        }
    }

    @Override
    public void preformState(ArrayList<DrawableObject> renderlist, ArrayList<Integer> keys, MousePos mouse) {
        // is the game started?
        if (!this.game_started){
            this.display_tutorial(renderlist, keys);
        } else if (this.failed){
            // display failure screen
            SimpleTextRender fail_main = new SimpleTextRender("You sorted an object into", 0, 20);
            SimpleTextRender fail_main2 = new SimpleTextRender("the wrong color!", 0, 50);
            String color = "It's color was: ";
            if (this.score_right == -1){
                color += "Red, put into Blue";
            } else if (this.score_left == -1){
                color += "Blue, put into Red";
            } else {
                color += "I have no idea, you must've cheated";
            }
            // make new object to display this
            SimpleTextRender how_failed = new SimpleTextRender(color, 0, 80);
            // finally
            SimpleTextRender press_button = new SimpleTextRender("Press Z to exit", 0, 110);
            // render them all
            renderlist.add(fail_main);
            renderlist.add(fail_main2);
            renderlist.add(how_failed);
            renderlist.add(press_button);
            // check for user input
            if (keys.contains(KeyEvent.VK_Z)){
                this.exitOnFail();
            }
        }else if (this.timer > 0) {
            // create objects
            SimpleTextRender score = new SimpleTextRender("Score: " + Integer.toString(this.score_left + this.score_right), 0, 20);
            SimpleTextRender time = new SimpleTextRender("Time Left: " + Long.toString(this.timer), 0, 40);
            SimpleImageRenderer background = new SimpleImageRenderer(this.background, 0, 100);
            // do we need to make a new obj?
            if (!this.obj_on_screen){
                // make new object
                int col = Main.random.nextInt(2);
                boolean color;
                // 0 is blue
                // 1 is red
                color = col != 0;
                // create the new object
                this.obj = new SortObject(color, 225, 225);
                // set to true
                this.obj_on_screen = true;
            } else {
                // only allow player input if the box is not moving
                if (!this.moving) {
                    // we will take player input here
                    if (keys.contains(KeyEvent.VK_LEFT)){
                        // set moving to true
                        this.moving = true;
                        // set direction to left
                        this.dir = MovementDirections.LEFT;
                    } else if (keys.contains(KeyEvent.VK_RIGHT)){
                        // set moving to true
                        this.moving = true;
                        // set direction to right
                        this.dir = MovementDirections.RIGHT;
                    }
                }
            }
            // do we need to do movement?
            if (this.moving) this.doObjMovement();
            // render them all to the screen
            renderlist.add(background);
            renderlist.add(score);
            renderlist.add(time);
            // only draw if the object is on screen
            if (this.obj_on_screen){
                renderlist.add(this.obj);
            }
            // update the timer if needed
            // get seconds of duration
            long secs = TimeUnit.MILLISECONDS.toSeconds(this.watch.getDuration().toMillis());
            // update timer
            this.timer = total_time - secs;
        } else {
            // setup the basic text displays
            SimpleTextRender timeout = new SimpleTextRender("Ran out of Time", 0, 20);
            SimpleTextRender needed_score = new SimpleTextRender("You needed to score " + required_score, 0, 50);
            // add together scores and see if its enough
            int total_score = this.score_left + this.score_right;
            SimpleTextRender got_score = new SimpleTextRender("You scored: " + total_score, 0, 80);
            // create result string
            String result;
            if (total_score < required_score){
                result = "You failed!";
            } else {
                result = "You passed!";
            }
            SimpleTextRender res = new SimpleTextRender(result, 0, 110);
            SimpleTextRender pressbuttom = new SimpleTextRender("Press Z to exit", 0, 140);
            // add all of this shit to the render pipeline
            renderlist.add(timeout);
            renderlist.add(needed_score);
            renderlist.add(got_score);
            renderlist.add(res);
            renderlist.add(pressbuttom);
            if (keys.contains(KeyEvent.VK_Z)) {
                if (total_score >= required_score) {
                    // set the flag to enable removal of the blockade
                    Main.state_storage.put("dung1_puz2_pass", true);
                    // switch the state back to the overworld
                    Main.current_state = GameStates.OVERWORLD.id;
                } else {
                    // they failed, do the things that happen when they fail
                    this.exitOnFail();
                }
            }
        }
    }

    // allow access to the failed variable
    public boolean didFailed(){
        return this.failed;
    }
    private void exitOnFail(){
        // need to setup the fail state in the overworld
        // display a textbox
        ReflectionUtils.displayTextbox("You failed the minigame! Time to perish!");
        // queue a battle
        ReflectionUtils.queueBattleOnOverworldReturn("unholywater", Main.random.nextInt(1) + 5);
        // we're done here, switch scenes
        Main.current_state = GameStates.OVERWORLD.id;
    }


    private void display_tutorial(ArrayList<DrawableObject> renderlist, List keys){
        // setup the text slicer
        if (this.slicer == null){
            this.slicer = new TextSlicer("Rules: Use left and right keys to sort Red and Blue objects. Red goes to the left, Blue to the right. If you make a mistake, you fail instantly.");
        }
        // make new textbox
        TextboxObject txt = new TextboxObject(this.slicer.getText());
        if (this.slicer.doneSlicing()){
            if (this.framecounter == 0){
                // flip arrow state
                this.textbox_arrow = !this.textbox_arrow;
                // add 13 frames to the counter
                this.framecounter += 13;
            } else {
                this.framecounter -= 1;
            }
            // check for input
            if (keys.contains(KeyEvent.VK_Z)){
                // get rid of the textbox
                this.game_started = true;
                this.framecounter = 0;
                // start the timer
                this.watch.start();
            }
        }
        // set arrow state
        txt.setArrowState(this.textbox_arrow);
        // draw textbox
        renderlist.add(txt);
    }

    /*
    to avoid cluttering the main state function,
    we will be putting that code here
     */
    private void doObjMovement(){
        // branch based on direction
        if (this.dir == MovementDirections.RIGHT){
            // get current xcoord
            if (this.obj.getX() < 501){
                // move it 40px
                this.obj.addToX(40);
            } else {
                // then its offscreen.
                this.obj_on_screen = false;
                // undo moving variable
                this.moving = false;
                this.processScoring(false);
            }
        } else if (this.dir == MovementDirections.LEFT){
            if  (this.obj.getX() > -50){
                // subtract 40px from x
                this.obj.addToX(-40);
            } else {
                // its offscreen
                this.obj_on_screen = false;
                // undo moving variable
                this.moving = false;
                this.processScoring(true);
            }
        }
    }
    private void processScoring(boolean side){
        // get current type of object
        boolean color = this.obj.isRed();
        // side var: false means right, true means left
        if (side){
            // check if object is red
            if (color){
                this.score_left += 1;
            } else {
                // WRONG COLOR!
                this.score_left = -1;
            }
        } else {
            // check if object is blue
            if (!color){
                this.score_right += 1;
            } else {
                // wrong color/side
                this.score_right = -1;
            }
        }
        // is either side's score set to -1?
        if (this.score_right == -1 || this.score_left == -1){
            if (Main.debugging) System.err.println("Failed! Red: " + this.score_left + " Blue: " + this.score_right);
            // stop the stopwatch
            this.watch.stop();
            // fail the player
            this.failed = true;
        }
    }
}
