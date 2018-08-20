package arkanoid;

import animation.AnimationRunner;
import animation.KeyPressStoppableAnimation;
import biuoop.DialogManager;
import biuoop.GUI;
import biuoop.KeyboardSensor;
import coregame.Counter;
import coregame.HighScoresTable;
import coregame.ScoreInfo;

import java.io.File;

/**
 * @author shaytzir
 *
 */
public class GameFlow {
   // private int width;
   // private int height;
    private Counter score;
    private Counter lives;
    private AnimationRunner runner;
    private GUI gui;
    private biuoop.KeyboardSensor keyboard;
    private int borderSize;
    private HighScoresTable table;

    /**
     * constructor.
     * @param gui a Gui
     * @param ar an Animator runner to run also the game flow
     * @param ks Keyboard sensor. same sensor for all levels
     * @param borderSize the height/width of the horizontal/vertical blocks borders
     * @param table an highscore table
     */
    public GameFlow(GUI gui, AnimationRunner ar,
                    KeyboardSensor ks, int borderSize, HighScoresTable table) {
        this.gui = gui;
        this.keyboard = ks;
        this.runner = ar;
        this.score = new Counter(0);
        this.lives = new Counter(3);
        this.borderSize = borderSize;
        this.table = table;
    }
     /**
     * runs the pre designed levels. if the lives are 0 in some point, the user loses and the program terminates
     * else the user wins (all blocks are removed)
     */
    public void runLevels() {
        DialogManager dialog = this.gui.getDialogManager();
        int firstSpeed = 100;
        int numLevel = 1;
        while (this.lives.getValue() != 0) {
            GameLevel level = new GameLevel(this.runner, this.gui, this.keyboard,
                    this.score, this.lives, numLevel, firstSpeed);
            level.initialize();
            //if there are still blocks to remove and the user has enough lives - keep the game goinf
            while ((this.lives.getValue() > 0) && (!level.isComplete())) {
                level.playOneTurn();
            }
            //if the lives ended at this point the user lost, gui closed.
            if (this.lives.getValue() == 0) {
                LosingScreen gameOver = new LosingScreen(this.keyboard, this.score);
                KeyPressStoppableAnimation gameOverKey =
                        new KeyPressStoppableAnimation(this.keyboard, this.keyboard.SPACE_KEY, gameOver);
                this.runner.run(gameOverKey);
                if (table.getRank(this.score.getValue()) <= table.size()) {
                    String name = dialog.showQuestionDialog("Name", "What is your name?", "");
                    table.add(new ScoreInfo(name, this.score.getValue()));
                    try {
                        table.save(new File("highscores"));
                    } catch (Exception ex) {
                        System.out.println("something went wrong");
                        System.exit(1);
                    }
                }
            } else {
                numLevel++;
                firstSpeed += 50;
            }
        }
    }
}