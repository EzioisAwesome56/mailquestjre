package com.alysoft.dankengine.renderer;

public class DankFont {
    private String fontname;
    // Based on AWT, so
    // 0 -> plain
    // TODO: the rest, if needed
    private int style;
    private int fontsize;

    public DankFont(String name, int style, int size){
        this.fontname = name;
        this.style = style;
        this.fontsize = size;
    }

    public int getFontsize() {
        return this.fontsize;
    }

    public int getStyle() {
        return this.style;
    }

    public String getFontname() {
        return this.fontname;
    }
}
