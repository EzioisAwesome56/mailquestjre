package com.eziosoft.mailquestjre.json;

import com.eziosoft.mailquestjre.MailQuestJRE;
import com.eziosoft.mailquestjre.stuff.SaveFileUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SaveFileMetaFile {

    // this only needs 2 objects
    // the save file (base 64 encoded)
    private String save_file;
    // and a checksum
    private long checksum;

    public SaveFileMetaFile(SaveFileData save){
        // convert the object to a base64 string
        this.save_file = Base64.getEncoder().encodeToString(MailQuestJRE.gson.toJson(save).getBytes(StandardCharsets.UTF_8));
        // calculate the checksum of the data
        this.checksum = SaveFileUtils.CalculateChecksum(this.save_file);
    }

    public String getSave_file() {
        return this.save_file;
    }
    public long getChecksum() {
        return this.checksum;
    }
}
