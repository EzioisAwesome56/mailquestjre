package com.eziosoft.mailquestjre.stuff;

import com.alysoft.dankengine.Main;

public class TextEntryPrompt {


    public TextEntryPrompt(String windowtittle) {
        Main.getFunctionalBackend().prepareTextPrompt(windowtittle);
    }

    // open the window and then return the string
    public String doPrompt() throws InterruptedException{
        return Main.getFunctionalBackend().promptForText();
    }
    // destroy once done
    public void destroy(){
        // do nothing
        Main.logInfo("Something tried to destroy a textentryprompt; consider making it not do that");
    }
}
