package invaders;

import arkanoid.Block;
import geometry.Point;
import io.Background;

import java.util.TreeMap;

/**
 * Created by shaytzir on 25/06/2017.
 */
public class Enemy extends Block {

    /**
     * Counstructor.
     * @param x x value
     * @param y y value
     * @param fill a filling for the enemey
     */
    public Enemy(int x, int y, TreeMap<String, Background> fill) {
        super(x, y, fill);
    }

    /**
     * changing enemy's position.
     * @param dx change in dx
     * @param dy change in dy
     */
    public void moveEnemy(int dx, int dy) {
        int newX = (int) (this.getCollisionRectangle().getUpperLeft().getX() + dx);
        int newY = (int) this.getCollisionRectangle().getUpperLeft().getY() + dy;
        Point newPoint = new Point(newX, newY);
        this.changeLocation(newPoint);
    }

    /**
     *
     * @return the enemey's worth
     */
    public int worthy() {
        return 100;
    }
}
