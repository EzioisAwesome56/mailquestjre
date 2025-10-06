package com.eziosoft.mailquestjre.stuff.predefs;

import com.alysoft.dankengine.Main;
import com.eziosoft.mailquestjre.MailQuestJRE;
import com.eziosoft.mailquestjre.gameStates.ObjectSortMinigameState;
import com.eziosoft.mailquestjre.stuff.PredefinedFunctions;
import com.eziosoft.mailquestjre.stuff.enums.GameStates;

public class StartObjSort extends PredefinedFunctions {
    @Override
    public void doPredef() {
        // check to see if they already won or not
        try {
            if ((boolean) MailQuestJRE.state_storage.get("dung1_puz2_pass")) {
                // we don't need to play the minigame again
                return;
            }
        } catch (NullPointerException ignored){}
        // get the obj sort state
        ObjectSortMinigameState state = (ObjectSortMinigameState) Main.getState(GameStates.OBJSORT.id);
        // reset the state
        state.resetState();
        // then, switch to that state
        Main.current_state = GameStates.OBJSORT.id;
    }
}
