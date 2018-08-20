package invaders;

import arkanoid.GameLevel;
import biuoop.DrawSurface;
import coregame.HitListener;
import geometry.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.LinkedList;

/**
 * Created by shaytzir on 25/06/2017.
 */
public class Formation {
    private int startX;
    private int startY;
    private int rows;
    private int columns;
    private int spaces;
    private int leftBound;
    private int rightBound;
    private int lowBound;
    private int defSpeed;
    private int changeSpeed;
    private double highest;
    private double lowest;
    private double mostLeft;
    private double mostRight;

    private List<FormationListener> listeners;
    private Random rand;

    private List<List<Enemy>> enemies;
    private Map<Enemy, Point> startP;

    /**
     * Counstructor.
     * @param left left border of screen
     * @param right right border of screen
     * @param low lower border for enemeis (shields place)
     * @param speed enemeies initial speed
     */
    public Formation(int left, int right, int low, int speed) {
        this.startX = 50;
        this.startY = 50;
        this.rows = 5;
        this.columns = 10;
        this.leftBound = left;
        this.rightBound = right;
        this.lowBound = low;
        this.defSpeed = speed;
        this.changeSpeed = speed;
        this.startP = new HashMap<Enemy, Point>();
        this.enemies = new ArrayList<>();

        this.listeners = new ArrayList<FormationListener>();
        this.rand = new Random();

        EnemyCreator creator = new EnemyCreator();
        //create all enemeies using enemy creator
        for (int i = 0; i < this.columns; i++) {
            int xNow = this.startX + i * 50;
            List<Enemy> enemyColumn = new LinkedList<Enemy>();

            for (int j = 0; j < this.rows; j++) {
                int yNow = this.startY + j * 40;
                Enemy newEnemy = creator.create(xNow, yNow);
                this.startP.put(newEnemy, new Point(xNow, yNow));
                enemyColumn.add(newEnemy);

            }
            this.enemies.add(enemyColumn);
        }
        //refreshing boundries
        this.knowBounderiesEnemy();
    }

    /**
     * making formation know it's bounderies.
     */
    public void knowBounderiesEnemy() {

        double leftOfall = Double.NEGATIVE_INFINITY;
        double rightOfall = Double.POSITIVE_INFINITY;
        double mostLow = Double.NEGATIVE_INFINITY;
        double mostHigh = Double.POSITIVE_INFINITY;

        for (List<Enemy> cols: this.enemies) {
            Enemy checkLow = cols.get(cols.size() - 1);
            if (checkLow.getUpperY() > mostLow) {
                mostLow = checkLow.getUpperY();
            }
            Enemy checkHigh = cols.get(0);
            if (checkHigh.getUpperY() < mostHigh) {
                mostHigh = checkHigh.getUpperY();
            }
        }
        this.highest = mostHigh;
        this.lowest = mostLow + 30;

        this.mostLeft = this.enemies.get(0).get(0).getUpperX();
        this.mostRight = this.enemies.get(this.enemies.size() - 1).get(0).getUpperX() + 40;
    }

    /**
     * returning all enemirs to their initial pose.
     */
    public void backToStartPose() {
        for (List<Enemy> cols: this.enemies) {
            for (Enemy enemy: cols) {
                enemy.changeLocation(this.startP.get(enemy));
            }
        }
        this.changeSpeed = this.defSpeed;
        this.knowBounderiesEnemy();
    }


    /**
     *
     * @param dt given dt, per frame
     */
    public void timePassed(double dt) {
        int dx = (int) (dt * this.changeSpeed);
        int dy = 0;

        boolean gotToX = false;
        if (dx > 0) {
            if (mostRight + dx > rightBound) {
                gotToX = true;
            }
        } else if (mostLeft + dx < leftBound) {
            gotToX = true;
        }
        //if collided with sides
        if (gotToX) {
            this.changeSpeed = (int) (this.changeSpeed * (-1.1));
            dx = 0;
            dy = 15;
            //if moving the enemies down, made them collide with shields, do somthing
            if (this.lowest + dy > this.lowBound) {
                fomationCollideShield();
            }
            this.lowest += dy;
        } else {
            this.mostRight += dx;
            this.mostLeft += dx;
        }
        for (List<Enemy> enemyColumn : this.enemies) {
            for (Enemy enemy : enemyColumn) {
                enemy.moveEnemy(dx, dy);
            }
        }
        if (this.numEnemies() > 0) {
            knowBounderiesEnemy();
        }
    }

    /**
     * method if enemeies collided shiels.
     */
    private void fomationCollideShield() {
        //call all listeners
        for (FormationListener lis: this.listeners) {
            lis.collideShield();
        }
    }

    /**
     *
     * @return random point (choose from the lowest enemeies of each column)
     */
    public Point shootRandomlyPoint() {
        int colNum = this.rand.nextInt(this.enemies.size());
        List<Enemy> col = this.enemies.get(colNum);
        Enemy bottom = col.get(col.size() - 1);
        return new Point(bottom.getUpperX() + 20, bottom.getUpperY() + 35);
    }

    /**
     *
     * @param d drawing surface
     */
    public void drawOn(DrawSurface d) {
        for (List<Enemy> col: this.enemies) {
            for (Enemy en: col) {
                en.drawOn(d);
            }
        }
    }

    /**
     *
     * @param level game level to add to
     */
    public void addTo(GameLevel level) {
        for (List<Enemy> column : this.enemies) {
            for (Enemy enemy : column) {
                level.addCollidable(enemy);
                level.addSprite(enemy);
            }
        }
    }

    /**
     *
     * @param level game level to remove from
     */
    public void removeFrom(GameLevel level) {
        for (List<Enemy> column : this.enemies) {
            for (Enemy enemy : column) {
                level.removeCollidable(enemy);
                level.removeSprite(enemy);
            }
        }
    }

    /**
     *
     * @param lis a listener to add to enemies
     */
    public void addHitLisToEnemies(HitListener lis) {
        for (List<Enemy> cols: this.enemies) {
            for (Enemy en: cols) {
                en.addHitListener(lis);
            }
        }
    }

    /**
     * removing enemy.
     * @param en an enemey.
     */
    public void removeEnemy(Enemy en) {
        List<Enemy> replacedCol = null;
        for (List<Enemy> col: this.enemies) {
            if (col.remove(en)) {
                replacedCol = col;
                break;
            }
        }
        if (replacedCol.size() == 0) {
            this.enemies.remove(replacedCol);
        }
        if (this.enemies.size() > 0) {
            this.knowBounderiesEnemy();
        }
        if (this.enemies.size() == 0) {
            allEnemiesDied();
        }
    }

    /**
     * calls this method if all of the enemies died.
     */
    public void allEnemiesDied() {
        for (FormationListener lis: this.listeners) {
            lis.deadEnemies();
        }

    }

    /**
     * removing listeners from all enemies.
     * @param lis listener
     */
    public void removeHitLisFromEnemies(HitListener lis) {
        for (List<Enemy> cols: this.enemies) {
            for (Enemy en: cols) {
                en.removeHitListener(lis);
            }
        }
    }

    /**
     *
     * @return the current number of existing enemies
     */
    public int numEnemies() {
        int num = 0;
        for (List<Enemy> col: this.enemies) {
            for (Enemy en: col) {
                num++;
            }
        }
        return num;
    }

    /**
     * adding listener.
     * @param listener a listener
     */
    public void addListener(FormationListener listener) {
        this.listeners.add(listener);
    }

    /**
     * removing a listener.
     * @param listener a listener
     */
    public void removeListener(FormationListener listener) {
        this.listeners.remove(listener);
    }
}
