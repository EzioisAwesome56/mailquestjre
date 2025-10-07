package com.alysoft.dankengine.backends.desktop;

import com.alysoft.dankengine.Main;
import com.alysoft.dankengine.backends.base.EngineBackend;
import com.alysoft.dankengine.renderer.DankColor;
import com.alysoft.dankengine.renderer.DankFont;
import com.alysoft.dankengine.renderer.DankGraphic;
import com.alysoft.dankengine.utility.DankButtons;
import com.alysoft.dankengine.utility.MousePos;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class DesktopBackend extends EngineBackend {
    private boolean debugging;
    private boolean use_hardware_accel;
    private boolean spamdebug;
    private BaseGame basegame;
    private Graphics2D gfx;
    private Image canvas;
    public DesktopBackend(String[] args, int width, int height, String windowtitle){
        System.out.println("DankEngine Desktop PC/AWT+Swing Backend v1.0");
        // TODO: parse cli options
        // we need this
        ImageIO.setUseCache(false);
        // very cheap and crappy argument parsing
        for (String s : args){
            s = s.toLowerCase();
            if (s.equals("-debug")){
                // enable debug mode
                this.debugging = true;
            }
            if (s.equals("-software")){
                System.out.println("Running using pure software renderer");
                use_hardware_accel = false;
            }
            if (s.equals("-spam")){
                // enable spam debugging stuff
                this.spamdebug = true;
            }
        }
        // setup a new window
        // if we are debugging, use a different window title
        JFrame frame;
        if (Main.debugging || windowtitle == null){
            frame = new JFrame("Dank Engine Development Build");
        } else {
            frame = new JFrame(windowtitle);
        }
        // create game panel
        this.basegame = new BaseGame();
        this.basegame.setPreferredSize(new Dimension(width, height));
        this.basegame.setDoubleBuffered(true);
        frame.getContentPane().add(this.basegame);
        // configure input
        frame.addKeyListener(this.basegame);
        // finish configuring window
        frame.setResizable(false);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        // done with backend spesific code
    }

    @Override
    public boolean writeTextFile(String filename, String content) {
        throw new RuntimeException("STUB");
    }

    @Override
    public boolean writeFile(String filename, byte[] content) {
        throw new RuntimeException("STUB");
    }

    @Override
    public String readTextFile(String filename) throws IOException {
        throw new RuntimeException("STUB");
    }

    @Override
    public boolean writeCrashLog(String content) {
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
            System.err.println("Error while trying to write crash log");
            e.printStackTrace();
            return false;
        }
        // and now we're done
        return true;
    }

    @Override
    public void errorHandler(Exception error) {
        // if we are running in debug mode, also print the stack trace
        // so that i can click line numbers and have intellij take me here
        if (this.debugging){
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
        errorframe.setLocationRelativeTo(this.basegame);
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
                writeCrashLog(full_trace_builder.toString());
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

    private Color convertDankToColor(DankColor color){
        Color realcolor;
        if (color.isHas_alpha()){
            realcolor = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        } else {
            realcolor = new Color(color.getRed(), color.getGreen(), color.getBlue());
        }
        return realcolor;
    }

    @Override
    public void drawRectangleFilled(int x, int y, int width, int height, DankColor color) {
        this.gfx.setColor(this.convertDankToColor(color));
        this.gfx.fillRect(x, y, width, height);
    }

    @Override
    public void drawRectangle(int x, int y, int width, int height, DankColor color) {
        this.gfx.setColor(this.convertDankToColor(color));
        this.gfx.drawRect(x, y, width, height);
    }

    private Font convertDankToFont(DankFont font){
        return new Font(font.getFontname(), font.getStyle(), font.getFontsize());
    }

    @Override
    public void drawTextSimple(int x, int y, DankFont font, DankColor color, String text) {
        this.gfx.setColor(this.convertDankToColor(color));
        this.gfx.setFont(this.convertDankToFont(font));
        this.gfx.drawString(text, x, y);
    }

    @Override
    public void setupDrawString(DankFont font, DankColor color) {
        this.gfx.setFont(this.convertDankToFont(font));
        this.gfx.setColor(this.convertDankToColor(color));
    }

    @Override
    public void drawString(String text, int x, int y) {
        this.gfx.drawString(text, x, y);
    }

    @Override
    public void setColor(DankColor color) {
        this.gfx.setColor(this.convertDankToColor(color));
    }

    @Override
    public AttributedString makeAttributeString(String content, DankFont font) {
        // we apparently need a hashmap for some of this shit
        Hashtable<TextAttribute, Object> map = new Hashtable<>();
        map.put(TextAttribute.FONT, this.convertDankToFont(font));
        return new AttributedString(content, map);
    }

    @Override
    public void drawTextAdvanced(int x, float y, AttributedCharacterIterator iterator, int breakwidth) {
        // stolen from the textbox object; i dunno how reusable this is
        int textstart = iterator.getBeginIndex();
        int textend = iterator.getEndIndex();
        FontRenderContext frc = this.gfx.getFontRenderContext();
        LineBreakMeasurer measurer = new LineBreakMeasurer(iterator, frc);
        // set the measurer to the start
        measurer.setPosition(textstart);
        // copy y to a new variable
        float ypos = y;
        // draw loop
        while (measurer.getPosition() < textend){
            TextLayout layout = measurer.nextLayout(breakwidth);
            // draw it
            layout.draw(this.gfx, x, ypos);
            // add to the y coord
            // the oracle example did not work so this is a thug'd solution
            ypos += (float) layout.getBounds().getHeight();
        }
    }

    @Override
    public void drawPolygon(int[] x, int[] y, int points, DankColor color) {
        this.gfx.setColor(this.convertDankToColor(color));
        this.gfx.drawPolygon(x, y, points);
    }

    @Override
    public void logInfo(String content) {
        System.out.println(content);
    }

    @Override
    public void logError(String content) {
        System.err.println(content);
    }

    @Override
    public void drawPolygonFilled(int[] x, int[] y, int points, DankColor color) {
        gfx.setColor(this.convertDankToColor(color));
        gfx.fillPolygon(x, y, points);
    }

    @Override
    public void drawCircle(int x, int y, int radius, DankColor Color) {
        throw new RuntimeException("STUB FUNCTION");
    }

    @Override
    public void drawCircleFilled(int x, int y, int radius, DankColor Color) {
        this.gfx.setColor(this.convertDankToColor(Color));
        this.gfx.fillOval(x, y, radius, radius);
    }

    @Override
    public void renderFrame() {
        // we will simply update the base game window
        this.basegame.provideFrame(this.canvas);
        this.basegame.repaint();
    }

    @Override
    public void createEmptyFrame(int height, int width) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
        if (this.use_hardware_accel){
            this.canvas = gc.createCompatibleVolatileImage(width, height, Transparency.OPAQUE);
        } else {
            this.canvas = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        }
        if (this.debugging && this.spamdebug) System.out.println("acceleration:" + this.canvas.getCapabilities(gc).isAccelerated());
        //Graphics2D gfx = canvas.createGraphics();
        this.gfx = (Graphics2D) this.canvas.getGraphics();
    }

    @Override
    public void cleanupGraphics() {
        this.gfx.dispose();
    }

    @Override
    public void flushFrame() {
        this.canvas.flush();
    }

    @Override
    public MousePos getMousePosition() {
        MousePos mouse_packet;
        try {
            mouse_packet = new MousePos(this.basegame.getMousePosition().x, this.basegame.getMousePosition().y);
        } catch (NullPointerException e){
            // assume mouse is not in the panel
            mouse_packet = new MousePos();
        }
        return mouse_packet;
    }

    @Override
    public ArrayList<Integer> getKeysDown() {
        List<Integer> keys_raw = this.basegame.getKeysdown();
        ArrayList<Integer> keys_dank = new ArrayList<>();
        // convert raw keys to DankButtons
        for (int i : keys_raw){
            switch (i){
                case KeyEvent.VK_RIGHT:
                    keys_dank.add(DankButtons.INPUT_RIGHT);
                    break;
                case KeyEvent.VK_DOWN:
                    keys_dank.add(DankButtons.INPUT_DOWN);
                    break;
                case KeyEvent.VK_UP:
                    keys_dank.add(DankButtons.INPUT_UP);
                    break;
                case KeyEvent.VK_LEFT:
                    keys_dank.add(DankButtons.INPUT_LEFT);
                    break;
                case KeyEvent.VK_Z:
                    keys_dank.add(DankButtons.INPUT_ACTION);
                    break;
                case KeyEvent.VK_X:
                    keys_dank.add(DankButtons.INPUT_CANCEL);
                    break;
                case KeyEvent.VK_ENTER:
                    keys_dank.add(DankButtons.INPUT_START);
                    break;
                default:
                    System.out.println("Unknown key input, not converting");
            }
        }
        return keys_dank;
    }

    @Override
    public String promptForText() throws InterruptedException {
        // reset the text box incase of repompt
        this.entry.setText("");
        this.frame.setVisible(true);
        // wait for button to be pressed
        synchronized (this.obj){
            this.obj.wait();
        }
        // close the window
        this.frame.setVisible(false);
        // unstick keys
        this.basegame.reset_keys();
        // destroy the frame before we're outta here
        this.frame.dispose();
        // once we're here, return the text
        return this.entry.getText();
    }

    // bunch of this was stolen directly from mailquest JRE
    private JFrame frame;
    private JTextField entry;
    public final Object obj = new Object();
    @Override
    public void prepareTextPrompt(String window_title) {
        // init the window
        this.frame = new JFrame(window_title);
        this.frame.setResizable(false);
        this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.frame.setLocationRelativeTo(this.basegame);
        // setup the layout manager
        this.frame.setLayout(new GridBagLayout());
        GridBagConstraints grid = new GridBagConstraints();
        grid.gridx = 0;
        grid.gridy = 0;
        // append  a label
        JLabel label = new JLabel(window_title);
        this.frame.add(label, grid);
        // make a new jpanel
        JPanel pan = new JPanel();
        // setup the text entry field
        this.entry = new JTextField(10);
        pan.add(this.entry);
        // append the  panel to the jframe
        grid.gridy += 1;
        frame.add(pan, grid);
        // create a button
        JButton but = new JButton("Done");
        but.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // alert the thread
                synchronized (obj){
                    obj.notifyAll();
                }
            }
        });
        // append to frame
        grid.gridy += 1;
        this.frame.add(but, grid);
        // pack the frame
        this.frame.pack();
    }

    @Override
    public void reset_keys() {
        this.basegame.reset_keys();
    }

    @Override
    public InputStream getEngineResource(String name) throws IOException {
        // try to open a stream to get the information
        return DesktopBackend.class.getResourceAsStream(name);
    }

    @Override
    public String getEngineTextResource(String name) throws IOException {
        // try to open a stream to get the information
        InputStream stream = this.getEngineResource(name);
        // read it to a string
        String tmp = IOUtils.toString(stream, StandardCharsets.UTF_8);
        // close the stream
        stream.close();
        // return the string
        return tmp;
    }

    @Override
    public Object getRawGraphicsAPI() {
        return this.gfx;
    }

    @Override
    public DankGraphic getEngineGraphicResource(String name) throws IOException {
        // first we need to get an input stream
        InputStream stream = this.getEngineResource(name);
        // read into a buffered image
        BufferedImage img = ImageIO.read(stream);
        // close the stream
        stream.close();
        // convert this object into a desktopGraphic
        DesktopGraphic deskgraph = new DesktopGraphic(img);
        return deskgraph;
    }

    @Override
    public DankGraphic generateNewGraphic(int width, int height, boolean transparent) {
        return new DesktopGraphic(height, width, transparent);
    }

    @Override
    public File getExternalFile(String filename) {
        return new File(filename);
    }
}
