package com.alysoft.dankengine.json;

public class AnimationData {
    /**
     * this class is for each individual animation. contains the following information
     * - number of frames
     * - filename of the png that contains all frames
     */
    private String filename;
    private int frames;

    public int getFrames() {
        return this.frames;
    }
    public String getFilename(){
        return this.filename;
    }
}
