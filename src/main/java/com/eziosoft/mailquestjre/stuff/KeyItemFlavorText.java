package com.eziosoft.mailquestjre.stuff;

import com.eziosoft.mailquestjre.Main;
import com.eziosoft.mailquestjre.json.KeyItemTextStorage;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class KeyItemFlavorText {
    private KeyItemTextStorage storage;

    // call this during game init or else everything will break
    public KeyItemFlavorText(){
        // attempt to load the file
        try {
            InputStream stream = KeyItemFlavorText.class.getResourceAsStream("/title/keyitems.json");
            // read to string
            String string = IOUtils.toString(stream, StandardCharsets.UTF_8);
            // close stream
            stream.close();
            // convert to object
            this.storage = Main.gson.fromJson(string, KeyItemTextStorage.class);
        } catch (IOException e){
            // explode
            // TODO: have early loads caught by an error handler or something
            System.err.println("oops the key item loader broke");
            e.printStackTrace();
            System.exit(2);
        }
    }

    public String getFlavorText(String item){
        String text;
        // does it even exist?
        if (this.storage.getKey_items().containsKey(item)){
            // get it
            text = this.storage.getKey_items().get(item);
        } else {
            text = "This key item has no flavor text to go along with it!";
        }
        // return that string
        return text;
    }
}
