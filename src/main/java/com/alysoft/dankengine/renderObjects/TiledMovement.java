package com.alysoft.dankengine.renderObjects;

import com.alysoft.dankengine.Main;
import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderer.DankColor;
import com.alysoft.dankengine.renderer.DankGraphic;
import com.alysoft.dankengine.utility.Camera;

import java.io.IOException;

public class TiledMovement implements DrawableObject {

    // tile sizes are defined in Main.tile_size
    private int tilex = 0;
    private int tiley = 0;
    private int counter = 0;
    // NEW: you can set a graphic instead of just being a red box
    private DankGraphic graphic;
    private boolean useGraphics;
    // how many pixels into a tile we are
    private int subtiles_x = 0;
    private int subtiles_y = 0;
    private boolean isMoving = false;
    /*
    0 -> right
    1 -> left
    2 -> down
    3 -> up
     */
    private int direction;

    public void ApplyMoveRight(){
        this.applyMovement(0);
    }
    public void ApplyMoveLeft(){
       this.applyMovement(1);
    }
    public void ApplyMoveDown(){
        this.applyMovement(2);
    }
    public void ApplyMoveUp(){
        this.applyMovement(3);
    }
    private void applyMovement(int direction){
        if (!this.isMoving){
            this.isMoving = true;
            this.direction = direction;
        }
    }

    /**
     * converts tilex to regular x coords based on math
     * @return raw x value
     */
    private int TileXtoX(){
        return this.tilex * Main.tile_size;
    }
    private int TileYtoY(){
        return this.tiley * Main.tile_size;
    }
    public int getX(){
        return this.TileXtoX() + this.subtiles_x;
    }
    public int getY(){
        return this.TileYtoY() + this.subtiles_y;
    }

    public TiledMovement(){
        this.useGraphics = false;
    }
    public TiledMovement(String resource){
        // ok, we need to load the resource we want now
        try {
            this.graphic = Main.getFunctionalBackend().getEngineGraphicResource(resource);
        } catch (IOException e){
            // something has gone horribly wrong
            throw new RuntimeException(e);
        }
        // FIXME: recreate the graphic in an image editor later
        //      for a POC, this will get the job done, however
        if (this.graphic.getWidth() < Main.tile_size){
            this.graphic = Main.getFunctionalBackend().upscaleGraphic(Main.tile_size, Main.tile_size, this.graphic);
            System.out.println("WARNING: a tiled movement object upscaled a graphic!");
        }
        // if we survived, enable graphic mode
        this.useGraphics = true;
    }

    // get tile coords
    public int getTilex() {
        return this.tilex;
    }
    public int getTiley(){
        return this.tiley;
    }
    // set tile coords
    public void setTileCoords(int x, int y){
        this.tilex = x;
        this.tiley = y;
    }
    // test this to see if a movement has completed or not
    public boolean isMoving(){
        return this.isMoving;
    }

    private final int movement_constant = 5;

    @Override
    public void drawObject(GraphicsBackend gfx, Camera camera) {
        // because this is ran every frame, we can run a little bit of update logic here
        if (this.isMoving){
            // check to see if we're done moving
            if ((Math.abs(this.subtiles_x) < Main.tile_size && this.direction < 2) || (Math.abs(this.subtiles_y) < Main.tile_size && this.direction >= 2)){
                if (this.counter == 1) {
                    // reset the counter
                    this.counter = 0;
                    // apply subpixel movement
                    switch (this.direction) {
                        case 0:
                            // move right 1 pixel
                            this.subtiles_x += this.movement_constant;
                            break;
                        case 1:
                            this.subtiles_x -= this.movement_constant;
                            break;
                        case 2:
                            this.subtiles_y += this.movement_constant;
                            break;
                        case 3:
                            this.subtiles_y -= this.movement_constant;
                            break;
                    }
                }
                // increment the counter by 1
                this.counter += 1;
            } else {
                // we're done moving, do the things
                // reset the sub tiles
                this.subtiles_x = 0;
                this.subtiles_y = 0;
                this.isMoving = false;
                // update tile pos
                switch (this.direction){
                    case 0:
                        this.tilex += 1;
                        break;
                    case 1:
                        this.tilex -= 1;
                        break;
                    case 2:
                        this.tiley += 1;
                        break;
                    case 3:
                        this.tiley -= 1;
                        break;
                }
            }
        }
        // regular drawing code goes here
        // CAMERA UPDATE: because we use this same calculation twice in code, why not just put it before the draw call?
        int draw_x = this.getX() - camera.getX();
        int draw_y = this.getY() - camera.getY();
        if (!this.useGraphics) {
            gfx.drawRectangleFilled(draw_x, draw_y, 25, 25, DankColor.red);
        } else {
            // draw the sprite we have loaded
            this.graphic.drawGraphic(draw_x, draw_y, gfx);
        }
    }
}
