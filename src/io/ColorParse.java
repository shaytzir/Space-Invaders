package io;

import java.awt.Color;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author shaytzir
 *
 */
public class ColorParse {
    private Map<String, Color> sToC;

    /**
     * constructor.
     */
    public ColorParse() {
        this.sToC = new TreeMap<String, Color>();
        this.sToC.put("black", Color.BLACK);
        this.sToC.put("blue", Color.BLUE);
        this.sToC.put("cyan", Color.CYAN);
        this.sToC.put("gray", Color.GRAY);
        this.sToC.put("lightGray", Color.LIGHT_GRAY);
        this.sToC.put("green", Color.GREEN);
        this.sToC.put("orange", Color.ORANGE);
        this.sToC.put("pink", Color.PINK);
        this.sToC.put("red", Color.RED);
        this.sToC.put("white", Color.white);
        this.sToC.put("yellow", Color.YELLOW);
    }

    /**
     *
     * @param s String which is a colors name
     * @return the color if its written as asked, exception if not exsist
     */
    public Color colorFromString(String s) {
        //what to do when getting a color which isnt in the list
        Color color = this.sToC.get(s);
        if (color == null) {
            System.out.println("Youre asking for a color which doesnt exist as asked");
            System.exit(1);
        }
        return color;
    }
}
