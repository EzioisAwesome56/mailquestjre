package com.eziosoft.mailquestjre.json;

import java.util.ArrayList;

public class CutsceneMetaFile {

    // this is the sub directory to look for graphics in
    private String subdir;
    // holds every frame needed to be displayed in a cutscene
    private ArrayList<CutsceneFrame> frames;
    /*
    optional; run a script upon cutscene completion
    set to "null" or null to not use this feature
     */
    private String script;

    public String getSubdir() {
        return this.subdir;
    }
    public ArrayList<CutsceneFrame> getFrames() {
        return this.frames;
    }
    public String getScript() {
        return this.script;
    }
}
