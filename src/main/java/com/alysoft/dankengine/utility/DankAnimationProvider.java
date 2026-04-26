package com.alysoft.dankengine.utility;

import com.alysoft.dankengine.Main;
import com.alysoft.dankengine.json.AnimationData;
import com.alysoft.dankengine.json.AnimationPackage;
import com.alysoft.dankengine.renderer.DankGraphic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DankAnimationProvider {

    // each animation id is mapped to the list of DankGraphics containing each animation frame
    private Map<Integer, List> loaded_anims;
    private int width;
    private int height;

    public Map<Integer, List> getLoaded_anims() {
        return this.loaded_anims;
    }
    public int get_numFrames(int id){
        return this.loaded_anims.get(id).size();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    /**
     * this is written with the assumption all sprites sheets are drawn in horizontal strips
     * so the y value is not adjusted
     * @param anim_data animation package json file
     * @param sprite_width width of sprites
     * @param sprite_height height of sprites
     */
    public DankAnimationProvider(String anim_data, int sprite_width, int sprite_height){
        this.width = sprite_width;
        this.height = sprite_height;
        // init the hashmap of loaded anims
        this.loaded_anims = new HashMap<>();
        // first, we need to load the json file containing all the animation data
        AnimationPackage data;
        try {
            String h = Main.getFunctionalBackend().getEngineTextResource(anim_data);
            data = Main.gson.fromJson(h, AnimationPackage.class);
        } catch (IOException e){
            throw new RuntimeException("Error trying to load animation data file: " + anim_data);
        }
        // ok now we have animation data, time to parse it
        for (Map.Entry<Integer, AnimationData> ent : data.getAnims().entrySet()){
            // get the basic data we need into managable variables
            String fname = ent.getValue().getFilename();
            int frames = ent.getValue().getFrames();
            // create a list to store all the frames in
            List<DankGraphic> loaded_frames = new ArrayList<>();
            // then, load the graphic in question
            DankGraphic sheet;
            try {
                sheet = Main.getFunctionalBackend().getEngineGraphicResource(data.getBase_folder() + fname);
            } catch (IOException e) {
                throw new RuntimeException("Error trying to load spritesheet", e);
            }
            // then cut up into frames
            for (int i = 0; i < frames; i++){
                DankGraphic temp = Main.getFunctionalBackend().cropImage(sprite_width, sprite_height, sprite_width * i, 0, sheet);
                // put it in a list
                loaded_frames.add(temp);
            }
            // unload the sheet
            sheet.flush();
            // put in the map
            this.loaded_anims.put(ent.getKey(), loaded_frames);
        }
        // should be done loading animations by now
    }
}
