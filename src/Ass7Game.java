import animation.AnimationRunner;
import animation.KeyPressStoppableAnimation;
import arkanoid.GameFlow;
import arkanoid.HighScoresAnimation;
import biuoop.GUI;
import biuoop.KeyboardSensor;
import coregame.HighScoresTable;
import menu.Menu;
import menu.MenuAnimation;
import menu.ShowHiScoresTask;
import menu.Task;

import java.io.File;

/**
 * Created by shaytzir on 25/06/2017.
 */
public class Ass7Game {
    /**
     * runs game.
     * @param args no input.
     */
    public static void main(String[] args) {
        GUI gui = new GUI("Space Invaders", 800, 600);
        HighScoresTable table = HighScoresTable.loadFromFile((new File("highscores")));
        KeyboardSensor keyboard = gui.getKeyboardSensor();
        AnimationRunner ar = new AnimationRunner(gui);
        HighScoresAnimation highScore = new HighScoresAnimation(table, keyboard.SPACE_KEY, keyboard);
        KeyPressStoppableAnimation highScoreKey =
                new KeyPressStoppableAnimation(keyboard, keyboard.SPACE_KEY, highScore);
        int borderBlockSize = 30;
        //creates menu and runs game
        Task<Void> runSelection = new Task<Void>() {
            public Void run() {
                GameFlow game = new GameFlow(gui, ar, keyboard, borderBlockSize, table);
                game.runLevels();
                ar.run(highScoreKey);
                return null;
            }
        };

        Menu<Task<Void>> menu = new MenuAnimation<Task<Void>>("Game Menu", keyboard, ar);

        Task<Void> highScoreTask = new ShowHiScoresTask(ar, highScoreKey);
        menu.addSelection("h", "High score", highScoreTask);

        menu.addSelection("s", "Start Game", runSelection);

        Task<Void> quitTask = new Task<Void>() {
            public Void run() {
                gui.close();
                System.exit(0);
                return null;
            }
        };

        menu.addSelection("q", "Quit", quitTask);

        while (true) {
            ar.run(menu);
            Task<Void> task = menu.getStatus();
            task.run();
        }

    }
}