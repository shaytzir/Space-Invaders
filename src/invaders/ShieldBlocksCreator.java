package invaders;

import arkanoid.Block;
import geometry.Point;
import geometry.Rectangle;
import io.Background;
import io.BlockCreator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by shaytzir on 25/06/2017.
 */
public class ShieldBlocksCreator implements BlockCreator {
    private int rows;
    private int numEach;

    /**
     * Constructor.
     */
    public ShieldBlocksCreator() {
        this.rows = 3;
        this.numEach = 28;
    }

    /**
     *
     * @param x start X
     * @param y start Y
     * @return a shield created by 3 rows and 28 columns of blocks
     */
    public List<Block> getShield(int x, int y) {
        List<Block> shield = new ArrayList<Block>();
        int xBackUp = x;
        int yBackUp = y;
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.numEach; j++) {
                Block b = this.create(xBackUp, yBackUp);
                shield.add(b);
                xBackUp = xBackUp + 5;
            }
            xBackUp = x;
            yBackUp = yBackUp + 5;
        }
        return shield;
    }

    /**
     *
     * @param xpos x position
     * @param ypos y position
     * @return a black block to be in shield
     */
    public Block create(int xpos, int ypos) {
        TreeMap<String, Background> fillMap = new TreeMap<String, Background>();
        Background back = new Background(new Color(0, 0, 0));
        fillMap.put("default", back);
        Rectangle rec = new Rectangle(new Point(xpos, ypos), 5, 5);
        return new Block(rec, fillMap, 1, null);
    }

}
