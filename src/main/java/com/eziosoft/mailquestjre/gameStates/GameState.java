package com.eziosoft.mailquestjre.gameStates;

import com.eziosoft.mailquestjre.renderObjects.DrawableObject;
import com.eziosoft.mailquestjre.stuff.MousePos;

import java.util.ArrayList;

public interface GameState {

    void preformState(ArrayList<DrawableObject> renderlist, ArrayList<Integer> keys, MousePos mouse);
}
