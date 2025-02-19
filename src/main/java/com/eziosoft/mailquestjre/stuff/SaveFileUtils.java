package com.eziosoft.mailquestjre.stuff;

import com.eziosoft.mailquestjre.Main;
import com.eziosoft.mailquestjre.gameStates.OverworldState;
import com.eziosoft.mailquestjre.json.SaveFileData;
import com.eziosoft.mailquestjre.json.SaveFileMetaFile;
import com.eziosoft.mailquestjre.stuff.enums.GameStates;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class SaveFileUtils {

    private static final String save_file_name = "save.json";
    private static final int save_version = 1;

    // this handles all the save file-related IO and shit like that

    // call this to attempt to load a save file
    public static void loadSaveFile(){
        // first, get the file of the actual  save file
        File savefile = getSaveFileFile(true);
        // load it into a string
        String content;
        try {
            // open an input stream
            FileInputStream stream = new FileInputStream(savefile);
            // use ioutils to read it to a string
            content = IOUtils.toString(stream, StandardCharsets.UTF_8);
            // close the stream
            stream.close();
        } catch (IOException e){
            // rethrow it lmao
            throw new RuntimeException("Error while trying to read save file!", e);
        }
        // then, use gson to convert to an object
        SaveFileMetaFile thefile = Main.gson.fromJson(content, SaveFileMetaFile.class);
        // next, we need to validate the data
        boolean iscorrupt = validateSaveFile(thefile.getSave_file(), thefile.getChecksum());
        if (!iscorrupt){
            throw new IllegalStateException("Save File is corrupted! Checksum does not match stored data!");
        }
        // next, we need to decode the base64 into a json string
        String decoded = new String(Base64.getDecoder().decode(thefile.getSave_file()), StandardCharsets.UTF_8);
        // convert this to a save File Object
        SaveFileData data = Main.gson.fromJson(decoded, SaveFileData.class);
        // TODO: at some point, there may be new save  file formats. Check the version and deal with it as needed
        // then, we can load all the values
        // load our player
        Main.player = data.getPlayer();
        Main.state_storage = data.getFlags();
        // tell the overworld state to load the current player map
        // but first, we need to get the object for it
        OverworldState overworld = (OverworldState) Main.getState(GameStates.OVERWORLD.id);
        // now we can tell it what to do
        overworld.setMapToLoad(data.getCurrent_map());
        overworld.forceMapReload();
        // also set the player's position on the map
        overworld.setPlayerCoords(data.getX(), data.getY());
        // in theory, we are now done with doing all the things
    }

    // called somewhere to check the hash/checksum of the saved data
    // helps avoid basic corruption
    private static boolean validateSaveFile(String data, long checksum){
        // get the long of the checksum of the stored data
        long new_check = CalculateChecksum(data);
        return new_check == checksum;
    }

    // just so that i dont have to copy and paste this code like 4 times or some
    // fucking bullshit like that
    public static long CalculateChecksum(String data){
        // calculate crc32 checksum of the data
        // loosely based on https://www.baeldung.com/java-checksums
        Checksum crc32 = new CRC32();
        crc32.reset();
        byte[] temp = data.getBytes(StandardCharsets.UTF_8);
        crc32.update(temp, 0, temp.length);
        return crc32.getValue();
    }

    // get the directory the save file should be in
    private static File getSaveFileFile(boolean samedir){
        if (samedir){
            return new File(save_file_name);
        } else {
            // TODO: this. handle the different user directories depending on the OS
            throw new IllegalStateException("This feature does not work yet! scammed!");
        }
    }

    // call this to delete your save file
    public static void deleteSaveFile(){
        // get the folder the save file is in
        File savefile = getSaveFileFile(true);
        // delete it
        boolean isgone = savefile.delete();
        if (!isgone){
            throw new RuntimeException("Error while deleting save file!");
        }
    }

    // very long and annoying code to check if there is a save file present
    // set the first argument to true to just check for a save file
    // in the same directory as the game jar
    public static boolean checkForSaveFile(){
        // check to see if the save file exists
        File thefile = getSaveFileFile(true);
        return thefile.exists() && thefile.isFile();
    }

    // this function will save the current game state to a file
    public static void CreateandWriteSaveFile(String currentmap, int x, int y){
        // create a new saveFileData object
        SaveFileData thedata = new SaveFileData();
        // store our data in it
        thedata.setVersion(save_version);
        thedata.setFlags(Main.state_storage);
        thedata.setPlayer(Main.player);
        thedata.setX(x);
        thedata.setY(y);
        thedata.setCurrent_map(currentmap);
        // create a new save file meta object for actually saving the file in
        SaveFileMetaFile tosave = new SaveFileMetaFile(thedata);
        // convert to json
        String json = Main.gson.toJson(tosave);
        // write to the file
        File writable = getSaveFileFile(true);
        try {
            // make output stream
            FileOutputStream stream = new FileOutputStream(writable);
            IOUtils.write(json.getBytes(StandardCharsets.UTF_8), stream);
            // close the stream
            stream.close();
            // done!
        } catch (IOException e){
            throw new RuntimeException("Error while trying to write save file!", e);
        }
    }
}
