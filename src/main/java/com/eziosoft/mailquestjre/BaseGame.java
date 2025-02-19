package com.eziosoft.mailquestjre;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class BaseGame extends JPanel implements KeyListener {

    private Image lastframe = null;
    private ArrayList<Integer> keysdown = new ArrayList<>();


    public BaseGame(){
        //
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.lastframe == null) {
            // bail the fuck out
            System.err.println("Provided frame was null");
            return;
        }
        // otherwise, draw the thing
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.lastframe, 0, 0, this);
        g2d.dispose();
        g.dispose();
    }
    public void provideFrame(Image img){
        // stupid fucking code to fix flickering maybe
        this.lastframe = img.getScaledInstance(500, -1, Image.SCALE_DEFAULT);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // todo: this
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println("key pressed!");
        // does it exist in the list?
        if (this.keysdown.contains(e.getKeyCode())){
            return; // do nothing
        } else {
            this.keysdown.add(e.getKeyCode());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //System.out.println("Key released!");
        if (this.keysdown.contains(e.getKeyCode())){
            // get index of item
            int index = this.keysdown.indexOf(e.getKeyCode());
            this.keysdown.remove(index);
        }
    }

    // get inputted keys
    public ArrayList<Integer> getKeysdown() {
        return this.keysdown;
    }
    // optional thing to help fix sticking keys
    public void reset_keys(){ this.keysdown.clear();}
}
