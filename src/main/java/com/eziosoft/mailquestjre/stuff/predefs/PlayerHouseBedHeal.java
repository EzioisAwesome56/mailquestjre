package com.eziosoft.mailquestjre.stuff.predefs;

import com.alysoft.dankengine.Main;
import com.eziosoft.mailquestjre.MailQuestJRE;
import com.eziosoft.mailquestjre.gameStates.OverworldState;
import com.eziosoft.mailquestjre.stuff.PredefinedFunctions;
import com.eziosoft.mailquestjre.stuff.ReflectionUtils;
import com.eziosoft.mailquestjre.stuff.enums.GameStates;

public class PlayerHouseBedHeal extends PredefinedFunctions {

    @Override
    public void doPredef() {
        // get the overworld game state
        OverworldState state = (OverworldState) Main.getState(GameStates.OVERWORLD.id);
        // this is ONLY called from the player's house inside, so we can set the map to be player_house
        MailQuestJRE.player.set_lasthealmap("player_house");
        // now we can actually heal the player
        MailQuestJRE.player.fully_heal();
        // display a textbox
        String text = "You lay down in your bed and take a nice, comfortable nap. You wake up feeling fully refreshed!";
        ReflectionUtils.displayTextbox(text, state);
        // and now we're done
    }
}
