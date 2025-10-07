package com.alysoft.dankengine.utility;

import java.util.HashMap;
import java.util.Map;

public class TextSlicer {

    private int last_index = 0;
    private String content;
    private int counter = 0;

    private static HashMap<String, String> replacable_strings;
    public static void registerString(String placeholder, String replacement){
        replacable_strings.put(placeholder, replacement);
    }

    public TextSlicer(String cont){
        for (Map.Entry<String, String> ent : replacable_strings.entrySet()){
            cont = cont.replaceAll(ent.getKey(), ent.getValue());
        }
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
