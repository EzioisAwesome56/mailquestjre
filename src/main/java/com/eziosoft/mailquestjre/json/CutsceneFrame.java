package com.eziosoft.mailquestjre.json;

public class CutsceneFrame {
    private String textbox_text;
    private boolean show_nameplate;
    private String name;
    // note: if set to reuse, will reuse image loaded during previous frame
    private String frame_img;

    public String getTextbox_text() {
        return this.textbox_text;
    }
    public String getFrame_img() {
        return this.frame_img;
    }
    public String getDisplayName() {
        return this.name;
    }
    public boolean isShow_nameplate() {
        return this.show_nameplate;
    }
}
