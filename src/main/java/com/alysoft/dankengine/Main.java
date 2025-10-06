package com.alysoft.dankengine;

import com.alysoft.dankengine.backends.base.BasicFunctionsBackend;
import com.alysoft.dankengine.backends.base.EngineBackend;
import com.alysoft.dankengine.backends.desktop.BaseGame;
import com.alysoft.dankengine.backends.desktop.DesktopBackend;
import com.alysoft.dankengine.gameStates.GameState;
import com.alysoft.dankengine.gameStates.testState;
import com.alysoft.dankengine.utility.MousePos;
import com.alysoft.dankengine.enums.GameStates;
import com.eziosoft.mailquestjre.MailQuestJRE;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import org.apache.commons.lang3.time.StopWatch;
import com.alysoft.dankengine.renderObjects.DrawableObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {
    /*
    useful items can go here. some of these are also function calls to the backend
     */
    public static Gson gson = new GsonBuilder().setPrettyPrinting().setStrictness(Strictness.STRICT).create();
    public static Random random = new Random();

    public static boolean exitgame = false;
    private static EngineBackend backend;
    public static void logInfo(String text){
        backend.logInfo(text);
    }
    public static void logError(String text){
        backend.logError(text);
    }
    public static BasicFunctionsBackend getFunctionalBackend(){
        return (BasicFunctionsBackend) backend;
    }

    private static final ArrayList<DrawableObject> objs = new ArrayList<>();

    // engine options, do not affect gameplay in any sserious way
    public static boolean debugging = false;
    public static boolean spamdebug = false;

    // game state list
    private static final ArrayList<GameState> states = new ArrayList<>();
    public static int current_state = GameStates.TESTSTATE.id;
    public static void addState(GameState state){
        states.add(state);
    }
    public static String window_title = "DankEngine";

    // some states may need to be accessible from another state
    // this will let you do that
    // consult GameStates.java for a proper list of game states
    public static GameState getState(int index){
        return states.get(index);
    }

    public static void main(String[] args) {
        // TODO: figure out a clean way to allow early loads without hard coding it
        MailQuestJRE.PreformEarlyInit();
        // init the backend
        backend = new DesktopBackend(args, 500, 500, window_title);
        // there are a couple of debug flags left, so handle those
        for (String s : args){
            s = s.toLowerCase();
            if (s.equals("-debug")){
                // enable debug mode
                debugging = true;
            }
            if (s.equals("-spam")){
                // enable spam debugging stuff
                spamdebug = true;
            }
        }
        // time to start running actual game code
        try {
            PreformInitialLoading();
            runGame();
        } catch (Exception e){
            // make our error handler deal with this
            backend.errorHandler(e);
        }
    }

    private static void PreformInitialLoading() throws Exception {
        // execute whatever code you have from your game's main file here
        MailQuestJRE.PreformInitialLoading();
    }

    // this is the main game loop, where all the logic is actually ran from
    private static void runGame() throws Exception {
        // make a stopwatch
        StopWatch timer = new StopWatch();
        // loop
        while (!exitgame){
            // start timer
            timer.start();
            // empty render list
            objs.clear();
            // produce the base game frame
            backend.createEmptyFrame(500, 500);
            // get key input
            ArrayList<Integer> keys = backend.getKeysDown();
            // attempt to get mouse position
            MousePos mouse_packet = backend.getMousePosition();
            // run game state
            states.get(current_state).preformState(objs, keys, mouse_packet);
            // now we can render the frame
            // draw all the objects in the array, in order
            for (DrawableObject o : objs){
                o.drawObject(backend);
            }
            // clean up
            backend.cleanupGraphics();
            // render next frame
            backend.renderFrame();
            // stop the render timer
            timer.stop();
            long rendertime = timer.getDuration().toNanos();
            //System.out.println("Frame took " + rendertime + " nanosec to render");
            // did we run faster then we thought?
            if (rendertime < 16670000L){
                long timetosleep = 16670000L - rendertime;
                try {
                    // TODO: change this logging or get rid of it entirely
                    if (debugging && spamdebug) logInfo("sleeping");
                    Thread.sleep(TimeUnit.NANOSECONDS.toMillis(timetosleep));
                } catch (InterruptedException ignored){}
            }
            // restart the timer
            timer.reset();
            // flush old image data
            backend.flushFrame();
        }
    }
}