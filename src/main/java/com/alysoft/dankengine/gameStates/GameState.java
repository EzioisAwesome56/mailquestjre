package com.alysoft.dankengine.gameStates;

import com.alysoft.dankengine.renderObjects.DrawableObject;
import com.alysoft.dankengine.utility.MousePos;

import java.util.ArrayList;

public interface GameState {

    void preformState(ArrayList<DrawableObject> renderlist, ArrayList<Integer> keys, MousePos mouse);
}
