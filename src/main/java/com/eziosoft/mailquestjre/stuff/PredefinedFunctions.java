package com.eziosoft.mailquestjre.stuff;

import com.eziosoft.mailquestjre.stuff.predefs.HospitalHeal;
import com.eziosoft.mailquestjre.stuff.predefs.MathChallengePredef;
import com.eziosoft.mailquestjre.stuff.predefs.PlayerHouseBedHeal;
import com.eziosoft.mailquestjre.stuff.predefs.StartObjSort;

import java.util.ArrayList;

public abstract class PredefinedFunctions {


    public abstract void doPredef();

    public static void runPredef(int id){
        functions.get(id).doPredef();
    }
    public static ArrayList<PredefinedFunctions> functions = new ArrayList<>();

    // RUN THIS BEFORE YOU RUN A PREDEF
    public static void predef_init(){
        functions.add(new HospitalHeal());
        functions.add(new MathChallengePredef());
        functions.add(new StartObjSort());
        functions.add(new PlayerHouseBedHeal());
    }
}
