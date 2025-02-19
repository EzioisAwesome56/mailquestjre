package com.eziosoft.mailquestjre.stuff.predefs;

import com.eziosoft.mailquestjre.Main;
import com.eziosoft.mailquestjre.gameStates.BattleState;
import com.eziosoft.mailquestjre.gameStates.OverworldState;
import com.eziosoft.mailquestjre.stuff.PredefinedFunctions;
import com.eziosoft.mailquestjre.stuff.ReflectionUtils;
import com.eziosoft.mailquestjre.stuff.TextEntryPrompt;
import com.eziosoft.mailquestjre.stuff.TextSlicer;
import com.eziosoft.mailquestjre.stuff.enums.GameStates;

import java.lang.reflect.Field;

public class MathChallengePredef extends PredefinedFunctions {
    @Override
    public void doPredef() {
        // check to see if we already passed this event
        try {
            if ((boolean) Main.state_storage.get("dung1_puz1_pass")) {
                // we don't need to do the math test again
                return;
            }
        } catch (NullPointerException ignored){}
        // setup a couple of basic things
        boolean failedone = false;
        boolean failedtwo = false;
        boolean failedthree = false;
        TextEntryPrompt prompt;
        String res;
        int temp;
        try {
        /*
        the first question will be (3 + 7) * 6
        answer is 60
         */
            prompt = new TextEntryPrompt("Please enter the answer for: (3 + 7) * 6");
            // display the dialog
            res = prompt.doPrompt();
            // convert string to int
            try {
                temp = Integer.parseInt(res);
            } catch (NumberFormatException e){
                // force sett temp to -1
                temp = -1;
            }
            // did they fuck up the math question?
            if (temp != 60) failedone = true;
            prompt.destroy();
            /*
            the next question will be 4^7 + (9 / 3^2)^2
            answer: 16385
             */
            prompt = new TextEntryPrompt("Please enter the answer for: 4^7 + (9 / 3^2)^2");
            res = prompt.doPrompt();
            // try to convert to int
            try {
                temp = Integer.parseInt(res);
            } catch (NumberFormatException e){
                temp = -1;
            }
            // did they fuck up?
            if (temp != 16385) failedtwo = true;
            prompt.destroy();
            /*
            the final question will be (4 % 2) - 7^5 + 8
            answer: -16799
             */
            prompt = new TextEntryPrompt("Please enter the answer for: (4 % 2) - 7^5 + 8");
            res = prompt.doPrompt();
            try {
                temp = Integer.parseInt(res);
            } catch (NumberFormatException e){
                temp = -1;
            }
            // did they fuck up?
            if (temp != -16799) failedthree = true;
        } catch (Exception e){
            throw new RuntimeException("something broke with the text entry prompt, sadge", e);
        }
        // ok, so we have now finished all the questions. time to see how they did
        String base = "YOU HAVE SUBMITTED TEST. RESULTS: ";
        base += "#1: " + (failedone ? "FAIL" : "PASS") + " ";
        base += "#2: " + (failedtwo ? "FAIL" : "PASS") + " ";
        base += "#3: " + (failedthree ? "FAIL" : "PASS") + " ";
        // big fat if statement to choose what happens next
        OverworldState overworld = (OverworldState) Main.getState(GameStates.OVERWORLD.id);
        if (!failedone && !failedtwo && !failedthree){
            base += " ----- VERDICT: PASS";
            // set the state flag for being able to pass the blockade
            Main.state_storage.put("dung1_puz1_pass", true);
        } else {
            base += " ----- VERDICT: FAIL!";
            // spawn a lv 5 high postman to fight
            ReflectionUtils.queueBattleOnOverworldReturn("highpost", 5);
            // do reflection shit to activate the battle
            try {
                Field battlequeue = OverworldState.class.getDeclaredField("battle_queued");
                // set the value to true
                battlequeue.setAccessible(true);
                battlequeue.set(overworld, true);
                // undo that
                battlequeue.setAccessible(false);
            } catch (Exception e){
                throw new RuntimeException("Error during MATH FAIL reflection", e);
            }
        }
        // then, we need to display a textbox
        // which requires more reflection shit
        try {
            Field lock = OverworldState.class.getDeclaredField("lockControls");
            Field slicer = OverworldState.class.getDeclaredField("slicer");
            Field textbox = OverworldState.class.getDeclaredField("isTextbox");
            // set them all accessible
            lock.setAccessible(true);
            slicer.setAccessible(true);
            textbox.setAccessible(true);
            // enable the control lock
            lock.set(overworld, true);
            lock.setAccessible(false);
            // set the text we want to display
            slicer.set(overworld, new TextSlicer(base));
            slicer.setAccessible(false);
            // enable the textbox flag
            textbox.set(overworld, true);
            textbox.setAccessible(false);
            // in theory, we should be done now
        } catch (Exception e){
            throw new RuntimeException("error during textbox setup reflection!", e);
        }
    }
}
