package com.alysoft.dankengine.utility;

public class TextSlicer {

    private int last_index = 0;
    private String content;
    private int counter = 0;

    public TextSlicer(String cont){
        // TODO: enable registering of replacable strings
        // replace all instances of <player> with the player's actual name
        //this.content = cont.replaceAll("<player>", Main.player.getName());
        this.content = cont;
    }

    public String getText(){
        this.counter += 1;
        // if the counter has increased 30 times, increase index
        if (this.counter == 1){
            this.counter = 0;
            this.last_index += 1;
        }
        if (this.last_index > this.content.length()){
            this.last_index = this.content.length();
        }
        return this.content.substring(0, this.last_index);
    }

    public boolean doneSlicing(){
        return this.last_index >= this.content.length();
    }
}
