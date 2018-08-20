package io;

import animation.AnimationRunner;
import animation.KeyPressStoppableAnimation;
import arkanoid.GameFlow;
import arkanoid.HighScoresAnimation;
import biuoop.GUI;
import coregame.HighScoresTable;
import menu.Menu;
import menu.MenuAnimation;
import menu.ShowHiScoresTask;
import menu.Task;

import java.io.File;

/**
 * Created by shaytzir on 21/06/2017.
 */
public class GameSet {
    private GUI gui;
    private  biuoop.KeyboardSensor keyboard;
    private AnimationRunner runner;
    private HighScoresTable table;
    private HighScoresAnimation highScore;
    private KeyPressStoppableAnimation highScoreKey;
    private Menu<Task<Void>> menuG;

    /**
     *
     */
    public GameSet() {
        this.gui = new GUI("Arkanoid", 800, 600);

        this.keyboard = gui.getKeyboardSensor();
        this.runner = new AnimationRunner(gui);
        this.table = HighScoresTable.loadFromFile((new File("highscores")));
        this.highScore = new HighScoresAnimation(table, keyboard.SPACE_KEY, keyboard);
        this.highScoreKey =
                new KeyPressStoppableAnimation(keyboard, keyboard.SPACE_KEY, highScore);
        this.menuG = createMenu();
    }

    /**
     *
     * @return a Menu
     */
    public Menu<Task<Void>> createMenu() {
        int borderBlockSize = 30;


            Task<Void> runSelection = new Task<Void>() {
                public Void run() {
                    GameFlow game = new GameFlow(gui, runner, keyboard, borderBlockSize, table);
                    game.runLevels();
                    runner.run(highScoreKey);
                    return null;
                }
            };

            Menu<Task<Void>> menu = new MenuAnimation<Task<Void>>("Game Menu", keyboard, runner);

            Task<Void> highScoreTask = new ShowHiScoresTask(runner, highScoreKey);
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
            return menu;
    }

    /**
     *
     */
    public void run() {
        while (true) {
            runner.run(this.menuG);
            Task<Void> task = menuG.getStatus();
            task.run();
        }
    }

}
