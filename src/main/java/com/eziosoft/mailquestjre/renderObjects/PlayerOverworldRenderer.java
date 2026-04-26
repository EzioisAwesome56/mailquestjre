package com.eziosoft.mailquestjre.renderObjects;

import com.alysoft.dankengine.Main;
import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.enums.MovementDirections;
import com.alysoft.dankengine.renderObjects.TiledMovement;
import com.alysoft.dankengine.renderer.DankGraphic;
import com.alysoft.dankengine.utility.Camera;
import com.alysoft.dankengine.utility.DankAnimationProvider;

public class PlayerOverworldRenderer extends TiledMovement {

    // stuff we need for this to function correctly
    private DankAnimationProvider animations;
    public PlayerOverworldRenderer(String anims, String fallback){
        // init the parent object
        super(fallback);
        // load our animations
        // because we are using the same size player sprites as tiles, we can be lazy here
        this.animations = new DankAnimationProvider(anims, Main.tile_size, Main.tile_size);
    }
    private int frame_counter = 0;
    private int anim_frame = 0;
    private boolean still_sprite = false;

    private int convertDirectionToAnimID(MovementDirections dir){
        if (dir == MovementDirections.RIGHT) {
            return 1;
        } else if (dir == MovementDirections.LEFT){
            return 2;
        } else if (dir == MovementDirections.DOWN){
            return 3;
        } else if (dir == MovementDirections.UP){
            return 4;
        }
        throw new RuntimeException("Invalid movement direction!");
    }

    @Override
    public void drawObject(GraphicsBackend gfx, Camera camera) {
        // get the direction the player is moving
        MovementDirections dir = this.getDirection();
        // the main animation update code only really needs to happen if the player is moving
        if (super.isMoving()) {
            if (this.frame_counter == 5) {
                // reset the frame counter
                this.frame_counter = 0;
                // increase the animation frame counter
                this.anim_frame++;
                // allows us to reuse code later
                // get animation id from the movement direction table
                int animation_id = this.convertDirectionToAnimID(dir);
                // check the maximum frame count for the selected animation
                if (this.anim_frame >= this.animations.get_numFrames(animation_id)) {
                    this.anim_frame = 1;
                }
                // update the animation of the movement object
                super.setGraphic((DankGraphic) this.animations.getLoaded_anims().get(animation_id).get(this.anim_frame));
                // unset the still frame flag
                this.still_sprite = false;
            } else {
                // add 1 to the counter
                this.frame_counter++;
            }
        } else {
            if (!this.still_sprite){
                // update player's idle sprite, using frame 0 of the walking animation
                int anim_id = this.convertDirectionToAnimID(dir);
                // reset the frame counter to be safe
                //this.frame_counter = 0;
                // update the graphic
                super.setGraphic((DankGraphic) this.animations.getLoaded_anims().get(anim_id).get(0));
                this.still_sprite = true;
            }
        }
        // resume regular code
        super.drawObject(gfx, camera);
    }
}
