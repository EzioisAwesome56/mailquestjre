package com.eziosoft.mailquestjre;

import com.eziosoft.mailquestjre.gameStates.*;
import com.eziosoft.mailquestjre.stuff.*;
import com.eziosoft.mailquestjre.stuff.enums.GameStates;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.StopWatch;
import com.eziosoft.mailquestjre.renderObjects.DrawableObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    // stuff that will be useful to have on hand later
    /*
    im stoopit as fuck and sometimes typo the json arrays in the files
    enabling strict mode should make gson explode if something like this happens. hopefully (please)
     */
    public static Gson gson = new GsonBuilder().setPrettyPrinting().setStrictness(Strictness.STRICT).create();
    public static Random random = new Random();

    public static boolean exitgame = false;
    private static BaseGame basegame = null;
    public static BaseGame getGameWindow(){ return basegame; }

    private static final ArrayList<DrawableObject> objs = new ArrayList<>();

    // engine options, do not affect gameplay in any sserious way
    private static boolean use_hardware_accel = true;
    public static boolean debugging = false;
    public static boolean spamdebug = false;

    // game state list
    private static final ArrayList<GameState> states = new ArrayList<>();
    public static int current_state = GameStates.TITLE.id;
    // game runtime memory
    public static HashMap<String, Object> state_storage = new HashMap<>();
    public static Player player;

    // other things for the engine to use
    public static KeyItemFlavorText keyitemtext;

    // some states may need to be accessible from another state
    // this will let you do that
    // consult GameStates.java for a proper list of game states
    public static GameState getState(int index){
        return states.get(index);
    }

    public static void main(String[] args) {
        // we need this
        ImageIO.setUseCache(false);
        // very cheap and crappy argument parsing
        for (String s : args){
            s = s.toLowerCase();
            if (s.equals("-debug")){
                // enable debug mode
                debugging = true;
            }
            if (s.equals("-software")){
                System.out.println("Running using pure software renderer");
                use_hardware_accel = false;
            }
            if (s.equals("-spam")){
                // enable spam debugging stuff
                spamdebug = true;
            }
        }
        // setup a new window
        // if we are debugging, use a different window title
        JFrame frame;
        if (Main.debugging){
            frame = new JFrame("Dank Engine Prototype");
        } else {
            frame = new JFrame("MailQuest: Java Edition");
        }
        // create game panel
        basegame = new BaseGame();
        basegame.setPreferredSize(new Dimension(500, 500));
        basegame.setDoubleBuffered(true);
        frame.getContentPane().add(basegame);
        // configure input
        frame.addKeyListener(basegame);
        // finish configuring window
        frame.setResizable(false);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        // time to start running actual game code
        try {
            PreformInitialLoading();
            runGame();
        } catch (Exception e){
            // make our error handler deal with this
            errorHandler(e);
        }
    }

    private static void PreformInitialLoading() throws Exception {
        // we moved a bunch of this crap down here so any loading
        // errors with them can be caught by our error handler instead of
        // just being thrown to main's error handler
        // setup some basic shit
        states.add(new OverworldState());
        states.add(new BattleState());
        // dumb stupid hack for later
        TitleScreenState state = new TitleScreenState();
        states.add(state);
        states.add(new ObjectSortMinigameState());
        states.add(new PlayerStatPageState());
        states.add(new ItemMenuState());
        states.add(new CutsceneState());
        // TODO: get rid of the test state
        states.add(new testState());
        // init predefs
        PredefinedFunctions.predef_init();
        keyitemtext = new KeyItemFlavorText();
        // check for the existence of a save file
        // TODO: handle using either user directories or the same folder as the main executable
        if (SaveFileUtils.checkForSaveFile()){
           // tell the title screen we have a save file
            state.has_save();
        }
    }

    // this is the main game loop, where all the logic is actually ran from
    private static void runGame() throws Exception {
        // make a stopwatch
        StopWatch timer = new StopWatch();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
        // loop
        while (!exitgame){
            // start timer
            timer.start();
            // empty render list
            objs.clear();
            // produce the base game frame
            Image canvas;
            if (use_hardware_accel){
                canvas = gc.createCompatibleVolatileImage(500, 500, Transparency.OPAQUE);
            } else {
                canvas = new BufferedImage(500, 500, BufferedImage.TYPE_4BYTE_ABGR);
            }
            if (debugging && spamdebug) System.out.println("acceleration:" + canvas.getCapabilities(gc).isAccelerated());
            //Graphics2D gfx = canvas.createGraphics();
            Graphics2D gfx = (Graphics2D) canvas.getGraphics();
            // get key input
            ArrayList<Integer> keys = basegame.getKeysdown();
            // attempt to get mouse position
            MousePos mouse_packet;
            try {
                mouse_packet = new MousePos(basegame.getMousePosition().x, basegame.getMousePosition().y);
            } catch (NullPointerException e){
                // assume mouse is not in the panel
                mouse_packet = new MousePos();
            }
            // run game state
            states.get(current_state).preformState(objs, keys, mouse_packet);
            // now we can render the frame
            // draw all the objects in the array, in order
            for (DrawableObject o : objs){
                o.drawObject(gfx);
            }
            // clean up
            gfx.dispose();
            // render next frame
            basegame.provideFrame(canvas);
            basegame.repaint();
            // stop the render timer
            timer.stop();
            long rendertime = timer.getDuration().toNanos();
            //System.out.println("Frame took " + rendertime + " nanosec to render");
            // did we run faster then we thought?
            if (rendertime < 16670000L){
                long timetosleep = 16670000L - rendertime;
                try {
                    if (debugging && spamdebug) System.out.println("sleeping");
                    Thread.sleep(TimeUnit.NANOSECONDS.toMillis(timetosleep));
                } catch (InterruptedException ignored){}
            }
            // restart the timer
            timer.reset();
            // flush old image data
            canvas.flush();
        }
    }

    // crash handler
    // instead of just crashing to uhh nothing
    // we want to display a dialog first, and then violently explode
    private static void errorHandler(Exception error){
        // if we are running in debug mode, also print the stack trace
        // so that i can click line numbers and have intellij take me here
        if (debugging){
            System.err.println(error.getMessage());
            error.printStackTrace();
        }
        // we need to get the stack trace as a string first off
        StringBuilder full_trace_builder = new StringBuilder();
        // append normal stack trace
        full_trace_builder.append(ExceptionUtils.getStackTrace(error));
        // add some extra text
        full_trace_builder.append(System.lineSeparator());
        full_trace_builder.append("Root Cause StackTrace begins below");
        full_trace_builder.append(System.lineSeparator());
        // get the full root cause stack trace
        String[] rootcause = ExceptionUtils.getRootCauseStackTrace(error);
        // use a for loop to append it to our string
        for (String frame : rootcause){
            full_trace_builder.append(frame);
            full_trace_builder.append(System.lineSeparator());
        }
        // we should also append some additional system information
        full_trace_builder.append(System.lineSeparator());
        full_trace_builder.append(System.lineSeparator());
        full_trace_builder.append("System information below:");
        full_trace_builder.append(System.lineSeparator());
        full_trace_builder.append("Operating System: " + SystemUtils.OS_NAME).append(System.lineSeparator());
        full_trace_builder.append("OS Version: " + SystemUtils.OS_VERSION).append(System.lineSeparator());
        full_trace_builder.append("OS Arch: " + SystemUtils.OS_ARCH);
        // start building a window
        JFrame errorframe = new JFrame("Dank Engine Crash Handler");
        errorframe.setLocationRelativeTo(basegame);
        errorframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // do not make it resizable
        errorframe.setResizable(false);
        // setup the layout manager for this frame
        errorframe.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0;
        g.gridx = 0;
        // make some fancy labels
        JLabel label = new JLabel("Sorry, an error has occurred causing Dank Engine to crash");
        errorframe.add(label, g);
        g.gridy += 1;
        // add label with just the error message
        JLabel emsg = new JLabel(error.getMessage());
        errorframe.add(emsg, g);
        g.gridy += 1;
        // create text field to store our stack trace
        JTextArea stacktrace = new JTextArea(20, 70);
        // set the text inside of it
        stacktrace.setText(full_trace_builder.toString());
        // this is not an editable field, so disable that
        stacktrace.setEditable(false);
        // now, put that into a scroll pane
        JScrollPane scrollPane = new JScrollPane(stacktrace);
        // always show both scroll bars
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        // append this to the frame as well
        errorframe.add(scrollPane, g);
        g.gridy += 1;
        // make a jpanel to hold more shit
        JPanel buttons = new JPanel();
        JButton save = new JButton("Save Crash Report");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // dumb to text file
                writeTextFile(full_trace_builder.toString());
            }
        });
        // also make close button
        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // exit right now
                System.exit(2);
            }
        });
        // add them to the panel
        buttons.add(save);
        buttons.add(close);
        // append  to big frame
        errorframe.add(buttons, g);
        g.gridy += 1;
        // pack the frame
        errorframe.pack();
        // display it
        errorframe.setVisible(true);
    }

    private static void writeTextFile(String content){
        /*
        apparently getting the current date is pain in ass so uh
        NO
        use miliseconds instead
         */
        long time = System.currentTimeMillis();
        String filename = "DankEngineCrash-" + Long.toString(time) + ".txt";
        // create the file
        File txtdump = new File(filename);
        // write string to the file
        try {
            // make a writer
            FileWriter writer = new FileWriter(txtdump);
            // write to that file
            IOUtils.write(content, writer);
            // get rid of the writer
            writer.close();
        } catch (IOException e){
            System.err.println("How did you cause an error during the error handler???????????????");
            e.printStackTrace();
        }
        // and now we're done
    }
}