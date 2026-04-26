package com.alysoft.dankengine.json;

import java.util.Map;

public class AnimationPackage {
    /**
     * contains an animation package,
     * which contains several sub animations, called animation data
     */
    private Map<Integer, AnimationData> anims;
    private String base_folder;

    public String getBase_folder() {
        return this.base_folder;
    }

    public Map<Integer, AnimationData> getAnims() {
        return this.anims;
    }
}
