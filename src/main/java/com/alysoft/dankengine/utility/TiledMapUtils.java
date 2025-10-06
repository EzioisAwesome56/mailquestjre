package com.alysoft.dankengine.utility;

import com.alysoft.dankengine.Main;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.StringReader;

public class TiledMapUtils {

    // decided to break this code out into its own class, just for
    // decluttering the main overworld state class
    public static int[][] loadTileMap(String filename) throws Exception {
        // first, we need to try to open the file as a stream
        InputStream stream = Main.getFunctionalBackend().getEngineResource("/overworld/data/maps/" + filename + ".tmx");
        // ok, now we have to attempt to parse the XML
        /*
        originally, this was supposed to use jaxb. that worked great!
        until i found out they FUCKING REMOVED IT from java 11 onwards. so the code
        cannot be a dropin thing to run on java 21 too
        so now we're basing it on the "domparser" from https://stackoverflow.com/a/5059411
        i really really fucking hate xml why is it easier to parse in fucking python then java
         */
        DocumentBuilderFactory fuckxml = DocumentBuilderFactory.newInstance();
        //fuckxml.setValidating(true);
        fuckxml.setIgnoringElementContentWhitespace(true);
        DocumentBuilder fuckdom = fuckxml.newDocumentBuilder();
        Document pureanger = fuckdom.parse(stream);
        // don't forget to close the stream
        stream.close();
        /* now, we have to try and get to the element we want i think
            the flow, in theory is
            -> root element -> layer element(s) -> data element(s) -> the data we need
         */
        NodeList argh = pureanger.getDocumentElement().getElementsByTagName("layer");
        if (argh.getLength() < 1){
            throw new NullPointerException("NodeList for map xml is empty!");
        }
        // next, data element's contents from the layer element
        String garbage = argh.item(0).getFirstChild().getNextSibling().getTextContent();
        if (Main.debugging){
            Main.getFunctionalBackend().logInfo("Obtained the following from XML Parsing:");
            Main.getFunctionalBackend().logInfo(garbage);
        }
        // ok, we now have the data we want. i hate xml
        // now we can deal with the CSV data. Cringe
        Iterable<CSVRecord> csvdata = CSVFormat.DEFAULT.parse(new StringReader(garbage));
        // anyway, now we setup the 2d array we need
        int[][] output = new int[20][20];
        int x = 0;
        int y = 0;
        for (CSVRecord record : csvdata){
            for (String content : record.toList()){
                // check if we're at the end of a row yet
                if (x < 20){
                    // no? then just put the data in
                    /*
                    HOTFIX: there is disagreement between actual tileset savedata
                    and the map for some reason. this might fix it?
                     */
                    output[y][x] = Integer.parseInt(content) - 1;
                    x++;
                } else {
                    // get out of this fucking for loop
                    break;
                }
            }
            // reset x
            x = 0;
            // add 1 to y
            y++;
        }
        // we're done i think, return it
        return output;
    }
}
