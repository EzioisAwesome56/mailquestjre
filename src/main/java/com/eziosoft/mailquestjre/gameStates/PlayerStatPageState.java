package com.eziosoft.mailquestjre.gameStates;

import com.alysoft.dankengine.Main;
import com.alysoft.dankengine.gameStates.GameState;
import com.alysoft.dankengine.renderObjects.DrawableObject;
import com.alysoft.dankengine.renderer.DankGraphic;
import com.alysoft.dankengine.utility.DankButtons;
import com.alysoft.dankengine.utility.MousePos;
import com.eziosoft.mailquestjre.MailQuestJRE;
import com.eziosoft.mailquestjre.renderObjects.PlayerStatRenderer;
import com.eziosoft.mailquestjre.renderObjects.SimpleImageRenderer;
import com.eziosoft.mailquestjre.stuff.enums.GameStates;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerStatPageState implements GameState {

    private DankGraphic portrait;

    // use the constructor to load the buffered image
    public PlayerStatPageState(){
        try {
            this.portrait = Main.getFunctionalBackend().getEngineGraphicResource("/title/player_stats.png");
        } catch (IOException e){
            throw new IllegalStateException("Failed to load stats screen graphic!", e);
        }
    }


    @Override
    public void preformState(ArrayList<DrawableObject> renderlist, ArrayList<Integer> keys, MousePos mouse) {
        // pretty much make a new stat render object
        PlayerStatRenderer stats = new PlayerStatRenderer(MailQuestJRE.player);
        // render it
        renderlist.add(stats);
        // also load the full-body graphic of the main character
        SimpleImageRenderer image = new SimpleImageRenderer(this.portrait, 301, 100);
        renderlist.add(image);
        // if x is pressed, quit this dialog
        if (keys.contains(DankButtons.INPUT_CANCEL)){
            // switch scenes back to the overworld
            Main.current_state = GameStates.OVERWORLD.id;
        }
    }
}
