package com.eziosoft.mailquestjre.stuff;

import com.alysoft.dankengine.Main;
import com.eziosoft.mailquestjre.MailQuestJRE;
import com.eziosoft.mailquestjre.json.KeyItemTextStorage;

import java.io.IOException;

public class KeyItemFlavorText {
    private KeyItemTextStorage storage;

    // call this during game init or else everything will break
    public KeyItemFlavorText(){
        // attempt to load the file
        try {
            // read to string
            String string = Main.getFunctionalBackend().getEngineTextResource("/title/keyitems.json");
            // close stream
            // convert to object
            this.storage = MailQuestJRE.gson.fromJson(string, KeyItemTextStorage.class);
        } catch (IOException e){
            // explode
            throw new RuntimeException("Error while trying to load keyitem flavor text", e);
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
