package com.eziosoft.mailquestjre.json;

public class MapEvent {

    private int x;
    private int y;
    /*
    events can be activated in a couple of different ways
    - automatic (0); as soon as you match the  x, y pos, it starts
    - button (1): only works if you press the use button on it
    - todo: fill out this
     */
    private int activation_type;
    /*
    events can also be enabled or disabled based on a flag stored somewhere
    this will probably be implimented as a hashmap or something
    - 0: disabled by flag
    - 1: enabled by flag
    - 2: no flag use
    flagname is self-explainitory
     */
    private int flaguse;
    private String flagname;
    /*
    type of event, there can be several types
    - 0: display text; uses event_text below
    - 1: run predef function, put predef id in event_text
    - 2: run script; put name in event_text
    - 3: run cutscene; put name in event_text
     */
    private int event_type;
    private String eventtext;

    // getters for each object defined here
    public int getX() {
        return this.x;
    }
    public String getFlagname() {
        return this.flagname;
    }
    public int getFlaguse() {
        return this.flaguse;
    }
    public int getActivation_type() {
        return this.activation_type;
    }
    public int getY() {
        return this.y;
    }
    public int getEvent_type() {
        return this.event_type;
    }
    public String getEventtext() {
        return this.eventtext;
    }
}
