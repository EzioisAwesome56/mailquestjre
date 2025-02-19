package com.eziosoft.mailquestjre.stuff;

import com.eziosoft.mailquestjre.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TextEntryPrompt {

    private String text;
    private JFrame frame;
    private JTextField entry;
    public final Object obj = new Object();

    public TextEntryPrompt(String windowtittle) {
        // init the window
        this.frame = new JFrame(windowtittle);
        this.frame.setResizable(false);
        this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.frame.setLocationRelativeTo(Main.getGameWindow());
        // setup the layout manager
        this.frame.setLayout(new GridBagLayout());
        GridBagConstraints grid = new GridBagConstraints();
        grid.gridx = 0;
        grid.gridy = 0;
        // append  a label
        JLabel label = new JLabel(windowtittle);
        this.frame.add(label, grid);
        // make a new jpanel
        JPanel pan = new JPanel();
        // setup the text entry field
        this.entry = new JTextField(10);
        pan.add(this.entry);
        // append the  panel to the jframe
        grid.gridy += 1;
        frame.add(pan, grid);
        // create a button
        JButton but = new JButton("Done");
        but.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // alert the thread
                synchronized (obj){
                    obj.notifyAll();
                }
            }
        });
        // append to frame
        grid.gridy += 1;
        this.frame.add(but, grid);
        // pack the frame
        this.frame.pack();
    }

    // open the window and then return the string
    public String doPrompt() throws InterruptedException{
        // reset the text box incase of repompt
        this.entry.setText("");
        this.frame.setVisible(true);
        // wait for button to be pressed
        synchronized (this.obj){
            this.obj.wait();
        }
        // close the window
        this.frame.setVisible(false);
        // unstick keys
        Main.getGameWindow().reset_keys();
        // once we're here, return the text
        return this.entry.getText();
    }
    // destroy once done
    public void destroy(){
        this.frame.dispose();
    }
}
