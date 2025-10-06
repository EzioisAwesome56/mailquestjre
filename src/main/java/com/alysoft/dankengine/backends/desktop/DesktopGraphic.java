package com.alysoft.dankengine.backends.desktop;

import com.alysoft.dankengine.backends.base.GraphicsBackend;
import com.alysoft.dankengine.renderer.DankColor;
import com.alysoft.dankengine.renderer.DankFont;
import com.alysoft.dankengine.renderer.DankGraphic;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class DesktopGraphic extends DankGraphic {
    public DesktopGraphic(int height, int width, boolean trans) {
        super(height, width, trans);
        if (trans){
            this.image_data = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        } else {
            this.image_data = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        }
    }
    public DesktopGraphic(){
        //
    }
    public DesktopGraphic(BufferedImage img){
        this.image_data = img;
    }

    private BufferedImage image_data;
    private Graphics2D g2d;

    @Override
    public void drawGraphic(int x, int y, GraphicsBackend gfx) {
        // check to make sure its the right thing
        if (!(gfx.getRawGraphicsAPI() instanceof Graphics2D)){
            throw new RuntimeException("ERROR: Wrong raw graphics API returned, you may not be using the desktop backend!");
        }
        // now we can actually get it
        Graphics2D g2d = (Graphics2D) gfx.getRawGraphicsAPI();
        // draw our image
        g2d.drawImage(this.image_data, x, y, null);
    }

    @Override
    public GraphicsBackend getDrawable() {
        this.g2d = (Graphics2D) this.image_data.getGraphics();
        return new DesktopGraphicDrawable();
    }

    @Override
    public void flush() {
        this.image_data.flush();
    }

    class DesktopGraphicDrawable implements GraphicsBackend {

        @Override
        public void drawRectangleFilled(int x, int y, int width, int height, DankColor color) {
            throw new RuntimeException("STUB");
        }

        @Override
        public void drawRectangle(int x, int y, int width, int height, DankColor color) {
            throw new RuntimeException("STUB");
        }

        @Override
        public void drawTextSimple(int x, int y, DankFont font, DankColor color, String text) {
            throw new RuntimeException("STUB");
        }

        @Override
        public void drawTextAdvanced(int x, float y, AttributedCharacterIterator iterator, int breakwidth) {
            throw new RuntimeException("STUB");
        }

        @Override
        public void drawPolygon(int[] x, int[] y, int points, DankColor color) {
            throw new RuntimeException("STUB");
        }

        @Override
        public void drawPolygonFilled(int[] x, int[] y, int points, DankColor color) {
            throw new RuntimeException("STUB");
        }

        @Override
        public void drawCircle(int x, int y, int radius, DankColor Color) {
            throw new RuntimeException("STUB");
        }

        @Override
        public void drawCircleFilled(int x, int y, int radius, DankColor Color) {
            throw new RuntimeException("STUB");
        }

        @Override
        public void setColor(DankColor color) {
            throw new RuntimeException("STUB");
        }

        @Override
        public void renderFrame() {
            throw new RuntimeException("STUB");
        }

        @Override
        public void createEmptyFrame(int height, int width) {
            throw new RuntimeException("STUB");
        }

        @Override
        public void cleanupGraphics() {
            g2d.dispose();
        }

        @Override
        public void flushFrame() {
            throw new RuntimeException("STUB");
        }

        @Override
        public AttributedString makeAttributeString(String content, DankFont font) {
            throw new RuntimeException("STUB");
        }

        @Override
        public void setupDrawString(DankFont font, DankColor color) {
            throw new RuntimeException("STUB");
        }

        @Override
        public void drawString(String text, int x, int y) {
            throw new RuntimeException("STUB");
        }

        @Override
        public Object getRawGraphicsAPI() {
            return g2d;
        }
    }
}
