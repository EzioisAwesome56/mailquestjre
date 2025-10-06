package com.alysoft.dankengine.backends.base;

import com.alysoft.dankengine.renderer.DankGraphic;
import com.alysoft.dankengine.utility.MousePos;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public interface BasicFunctionsBackend {
    boolean writeTextFile(String filename, String content);
    boolean writeFile(String filename, byte[] content);
    String readTextFile(String filename) throws IOException;
    boolean writeCrashLog(String content);
    void errorHandler(Exception error);
    MousePos getMousePosition();
    ArrayList<Integer> getKeysDown();
    void logInfo(String content);
    void logError(String content);
    void reset_keys();
    void prepareTextPrompt(String window_title);
    String promptForText() throws InterruptedException;
    InputStream getEngineResource(String name) throws IOException;
    String getEngineTextResource(String name) throws IOException;
    DankGraphic getEngineGraphicResource(String name) throws IOException;
    DankGraphic generateNewGraphic(int width, int height, boolean transparent);
    File getExternalFile(String filename);
}
