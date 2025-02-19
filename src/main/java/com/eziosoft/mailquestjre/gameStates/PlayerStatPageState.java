package com.eziosoft.mailquestjre.gameStates;

import com.eziosoft.mailquestjre.Main;
import com.eziosoft.mailquestjre.renderObjects.DrawableObject;
import com.eziosoft.mailquestjre.renderObjects.PlayerStatRenderer;
import com.eziosoft.mailquestjre.renderObjects.SimpleImageRenderer;
import com.eziosoft.mailquestjre.stuff.MousePos;
import com.eziosoft.mailquestjre.stuff.enums.GameStates;

import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class PlayerStatPageState implements GameState{

    private BufferedImage portrait;

    // use the constructor to load the buffered image
    public PlayerStatPageState(){
        try {
            InputStream stream = PlayerStatPageState.class.getResourceAsStream("/title/player_stats.png");
            // use imageio to read that shit into memory
            this.portrait = ImageIO.read(stream);
            // close the stream
            stream.close();
        } catch (IOException e){
            throw new IllegalStateException("Failed to load stats screen graphic!", e);
        }
    }


    @Override
    public void preformState(ArrayList<DrawableObject> renderlist, ArrayList<Integer> keys, MousePos mouse) {
        // pretty much make a new stat render object
        PlayerStatRenderer stats = new PlayerStatRenderer(Main.player);
        // render it
        renderlist.add(stats);
        // also load the full-body graphic of the main character
        SimpleImageRenderer image = new SimpleImageRenderer(this.portrait, 301, 100);
        renderlist.add(image);
        // if x is pressed, quit this dialog
        if (keys.contains(KeyEvent.VK_X)){
            // switch scenes back to the overworld
            Main.current_state = GameStates.OVERWORLD.id;
        }
    }
}
