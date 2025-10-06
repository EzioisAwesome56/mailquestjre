package com.alysoft.dankengine.backends.base;

import com.alysoft.dankengine.renderer.DankColor;
import com.alysoft.dankengine.renderer.DankFont;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;

public interface GraphicsBackend {
    void drawRectangleFilled(int x, int y, int width, int height, DankColor color);
    void drawRectangle(int x, int y, int width, int height, DankColor color);
    void drawTextSimple(int x, int y, DankFont font, DankColor color, String text);
    void drawTextAdvanced(int x, float y, AttributedCharacterIterator iterator, int breakwidth);
    void drawPolygon(int[] x, int[] y, int points, DankColor color);
    void drawPolygonFilled(int[] x, int[] y, int points, DankColor color);
    void drawCircle(int x, int y, int radius, DankColor Color);
    void drawCircleFilled(int x, int y, int radius, DankColor Color);
    void setColor(DankColor color);
    void renderFrame();
    void createEmptyFrame(int height, int width);
    void cleanupGraphics();
    void flushFrame();
    AttributedString makeAttributeString(String content, DankFont font);
    void setupDrawString(DankFont font, DankColor color);
    void drawString(String text, int x, int y);
    Object getRawGraphicsAPI();
}
