package com.alysoft.dankengine.renderer;

/**
 * AWT doesn't exist on every platform, so to get around this we will be
 * making our own replacement classes to be converted into the native format
 * at runtime
 *
 * for example: this will replace awt.color for most cases
 */
public class DankColor {

    // defines for holding the color
    private int redchannel;
    private int bluechannel;
    private int greenchannel;
    private boolean has_alpha;
    private int alphachannel;

    // object constructors
    public DankColor(int red, int green, int blue){
        this.has_alpha = false;
        this.redchannel = red;
        this.greenchannel = green;
        this.bluechannel = blue;
    }
    public DankColor(int red, int green, int blue, int alpha){
        this.has_alpha = true;
        this.redchannel = red;
        this.bluechannel = blue;
        this.greenchannel = green;
        this.alphachannel = alpha;
    }

    // getters for values
    public int getAlpha() {
        return this.alphachannel;
    }
    public boolean isHas_alpha() {
        return this.has_alpha;
    }
    public int getGreen() {
        return this.greenchannel;
    }
    public int getBlue() {
        return this.bluechannel;
    }
    public int getRed() {
        return this.redchannel;
    }

    /**
     * AWT comes with some predefined colors built in,
     * this is simply copying them from JDK source into here
     * for completeness sake
     */
    public static final DankColor white     = new DankColor(255, 255, 255);
    public static final DankColor lightGray = new DankColor(192, 192, 192);
    public static final DankColor gray      = new DankColor(128, 128, 128);
    public static final DankColor darkGray  = new DankColor(64, 64, 64);
    public static final DankColor black     = new DankColor(0, 0, 0);
    public static final DankColor red       = new DankColor(255, 0, 0);
    public static final DankColor pink      = new DankColor(255, 175, 175);
    public static final DankColor orange    = new DankColor(255, 200, 0);
    public static final DankColor yellow    = new DankColor(255, 255, 0);
    public static final DankColor green     = new DankColor(0, 255, 0);
    public static final DankColor magenta   = new DankColor(255, 0, 255);
    public static final DankColor cyan      = new DankColor(0, 255, 255);
    public static final DankColor blue      = new DankColor(0, 0, 255);
}
