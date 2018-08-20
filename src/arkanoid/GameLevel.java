package arkanoid;

import animation.Animation;
import animation.AnimationRunner;
import animation.KeyPressStoppableAnimation;
import biuoop.DrawSurface;
import biuoop.GUI;
import coregame.Sprite;
import coregame.Collidable;
import coregame.HitListener;
import coregame.Velocity;
import coregame.Counter;

import geometry.Point;
import geometry.Rectangle;
import invaders.Enemy;
import invaders.Formation;
import invaders.FormationListener;

import invaders.ShieldBlocksCreator;
import invaders.HitPListener;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shaytzir
 *
 */
public class GameLevel implements Animation {
    private SpriteCollection sprites;
    private GameEnvironment environment;
    private int width;
    private int height;
    private GUI gui;
    private biuoop.KeyboardSensor keyboard;
    private int borderLength;

    private Paddle paddle;
    private Counter blockCount;
    private Counter ballCount;
    private Counter score;
    private Counter lives;
    private AnimationRunner runner;
    private boolean running;
    private int formationSpeed;

    private Formation formation;


    private double shootForPlayer;
    private double shootForFormation;
    private List<Ball> playerB;
    private List<Ball> enemyB;

    private boolean complete;
    private  int levelNum;

    private Background backForGame;
    private Background paddleBack;
    /**
     * constructor.
     * @param ar the anumation runner
     * @param gui a gui
     * @param key keyboard sensor
     * @param score score Counter
     * @param lives Lives Counter
     * @param levelNum the num of the level
     * @param formationSpeed the intial speed for enemies
     */
     public GameLevel(AnimationRunner ar,
             GUI gui, biuoop.KeyboardSensor key, Counter score, Counter lives, int levelNum, int formationSpeed) {
          this.sprites = new SpriteCollection();
          this.environment = new GameEnvironment();
          //can be changed;
          this.width = 800;
          this.height = 600;
          this.gui = gui;
          this.keyboard = key;
          this.levelNum = levelNum;
          this.blockCount = new Counter(0);
          this.ballCount = new Counter(0);
          this.score = score;
          this.lives = lives;
          this.runner = ar;
          this.running =  true;
          this.formationSpeed = formationSpeed;
          this.formation = null;

          this.playerB = new ArrayList<Ball>();
          this.enemyB = new ArrayList<Ball>();

          this.shootForPlayer = 0.35;
          this.shootForFormation = 0.5;

          this.complete = false;

          this.backForGame = new Background(Color.darkGray);
          this.backForGame.addToGame(this);
         java.awt.image.BufferedImage img = null;
         java.io.InputStream is = null;
         try {
             is = ClassLoader.getSystemClassLoader().getResourceAsStream("definitions/dalmatian1.jpg");
             img = javax.imageio.ImageIO.read(is);
         } catch (IOException e) {
             System.out.println("cant load image");
             System.exit(1);
         }
         this.paddleBack = new Background(img);
     }

     /**
      *
      * @return the current number of blocks in the level
      */
     public int getBlockNum() {
         return this.blockCount.getValue();
     }


    /**
     * creates a block which deletes balls hitting it.
     */
    public void createDeathRegions() {
        BallRemover ballRemove = new BallRemover(this, this.ballCount);
        Block deathRegionAlien = new Block(new Rectangle(
                new Point(0, this.height + 10),
                this.width, this.borderLength), Color.gray, 10);

        Block deathRegionPaddle = new Block(new Rectangle(
                new Point(0, -(this.borderLength)), this.width, this.borderLength + 30), Color.gray, 10);

        deathRegionPaddle.addToGame(this);
        deathRegionPaddle.addHitListener(ballRemove);
        deathRegionAlien.addToGame(this);
        deathRegionAlien.addHitListener(ballRemove);
    }
     /**
      * adds a collidable to the collidable array.
      * @param c collidable
      */
    public void addCollidable(Collidable c) {
         this.environment.getarray().add(c);
    }
    /**
     * adds a sprite to the sprite array.
     * @param s sprite
     */
    public void addSprite(Sprite s) {
         this.sprites.addSprite(s);
    }

    /**
     * the method removes the requested Collidable from the collidable collection (environment).
     * @param c a collideable we want to remove
     */
    public void removeCollidable(Collidable c) {
        this.environment.getarray().remove(c);
    }

    /**
     * the method removes the requested sprite from the sprite collection.
     * @param s a sprite we want to remove from the game
     */
    public void removeSprite(Sprite s) {
        this.sprites.getAllSprite().remove(s);
    }


    /**
     * Initialize a new game: create the Blocks and Ball (and Paddle)
     * and add them to the game.
     */
    public void initialize() {
        createDeathRegions();
        this.formation = new Formation(0, this.width, 500, this.formationSpeed);
        this.formation.addTo(this);
        GameLevel outerAccess = this;
        this.formation.addHitLisToEnemies(new HitListener() {
            @Override
            public void hitEvent(Block beingHit, Ball hitter) {
                Enemy en = (Enemy) beingHit;
                //en.removeFromGame(outerAccess);
                hitter.removeFromGame(outerAccess);
                if (playerB.contains(hitter)) {
                    formation.removeEnemy(en);
                    en.removeFromGame(outerAccess);
                    en.removeHitListener(this);

                    score.increase(en.worthy());
                }
                playerB.remove(hitter);
                if (outerAccess.formation.numEnemies() > 0) {
                    outerAccess.formation.knowBounderiesEnemy();
                }
            }
        });


        this.formation.addListener(new FormationListener() {
            @Override
            public void collideShield() {
                outerAccess.lives.decrease(1);
                running = false;
            }

            @Override
            public void deadEnemies() {
                running = false;
                complete = true;
            }

        });

        //create shield
        ShieldBlocksCreator shieldC = new ShieldBlocksCreator();
        int startX = 100;
        for (int i = 0; i < 3; i++) {
            List<Block> shieldList = shieldC.getShield(startX + i * 200, 500);
            for (Block shield : shieldList) {
                shield.addHitListener(new HitListener() {
                    @Override
                    public void hitEvent(Block beingHit, Ball hitter) {
                        if (beingHit.getStartHits() - beingHit.getCurrentHits() == 0) {
                            beingHit.removeHitListener(this);
                            beingHit.removeFromGame(outerAccess);
                        }
                        hitter.removeFromGame(outerAccess);
                        playerB.remove(hitter);
                    }
                });
                shield.addToGame(this);
            }
        }

        ScoreIndicator scoring = new ScoreIndicator(this.score);
        scoring.addToGame(this);

       NameIndicator name = new NameIndicator("Level's Name: battle #" + this.levelNum);
       name.addToGame(this);

        LivesIndicator livesNum = new LivesIndicator(this.lives);
        livesNum.addToGame(this);


        ScoreTrackingListener scoreTrack = new ScoreTrackingListener(this.score);

    }

    /**
     *
     * @return if this level is done or not
     */
    public boolean isComplete() {
        if (this.complete) {
            return true;
        }
        return false;
    }

    /**
     * runs the game.
     */
    public void run() {
        while (this.lives.getValue() > 0) {
            playOneTurn();
            this.lives.decrease(1);
        }
        this.gui.close();
    }

    /**
     *
     * @return a new paddle in the center of the screen.
     */
    public Paddle createPaddle() {
        int paddleWidth = 70;
        int paddlehHeight = (this.height - (2 * this.borderLength)) / 25;
        Paddle paddleGame = new Paddle(new Rectangle(new Point((this.width / 2) - (paddleWidth / 2),
                  this.height - 40), paddleWidth, paddlehHeight),
                  this.paddleBack, this.environment, this.width,
                  this.keyboard, 0, 500);
        return paddleGame;
    }

    /**
     *

    /**
     * adding the balls to this level.
     * @param balls a list of balls of this game
     */
    public void addBallsToGame(List<Ball> balls) {
        for (int j = 0; j < balls.size(); j++) {
            this.ballCount.increase(1);
            balls.get(j).addToGame(this);
        }
    }

    /**
     * run one turn game and start the animation loop.
     */
    public void playOneTurn() {
        this.paddle = this.createPaddle();
        GameLevel outerCopy = this;
        this.paddle.addHitPListener(new HitPListener() {
            @Override
            public void hitEvent(Paddle beingHit, Ball hitter) {
                hitter.removeFromGame(outerCopy);
                outerCopy.running = false;
                outerCopy.lives.decrease(1);
                enemyB.remove(hitter);
            }
        });
        this.paddle.addToGame(this);


        this.runner.run(new CountdownAnimation(2, 3, this.sprites));
        this.running = true;
        this.runner.run(this);
        for (Ball b: playerB) {
            b.removeFromGame(this);
        }
        for (Ball b: enemyB) {
            b.removeFromGame(this);
        }
        this.paddle.removeFromGame(this);
        if (!this.complete) {
            this.formation.backToStartPose();
        }
    }
    /**
     * the method keeps the game logics.
     * @param d a drawing surface
     * @param dt - seconds passed since last call
     */
    public void doOneFrame(DrawSurface d, double dt) {
        this.shootForPlayer -= dt;
        this.shootForFormation -= dt;

         this.sprites.drawAllOn(d);
         if ((this.keyboard.isPressed("space")) && (this.shootForPlayer < 0))  {
             playerFire();
         }
         if (this.shootForFormation < 0) {
             this.enemyFire(formation.shootRandomlyPoint());
         }
        this.formation.timePassed(dt);
         this.sprites.notifyAllTimePassed(dt);


         if (this.keyboard.isPressed("p")) {
             PauseScreen pause = new PauseScreen(this.keyboard);
             KeyPressStoppableAnimation pauseK =
                     new KeyPressStoppableAnimation(this.keyboard, this.keyboard.SPACE_KEY, pause);
             this.runner.run(pauseK);
          }
    }

    /**
     * let the player shoot a ball.
     */
    public void playerFire() {
        this.shootForPlayer = 0.35;
        Ball playerBall = new Ball(this.paddle.getXCenter(), this.paddle.getY() - 5, 5, Color.RED, this.environment);
        playerBall.setVelocity(Velocity.fromAngleAndSpeed(0, 500));
        playerBall.addToGame(this);
        this.playerB.add(playerBall);

    }

    /**
     * makes enemies shot a ball.
     * @param position a point for the enemy to shoot from
     */
    public void enemyFire(Point position) {
        this.shootForFormation = 0.5;
        Ball enemyBall = new Ball(position, 5, Color.blue, this.environment);
        enemyBall.setVelocity(Velocity.fromAngleAndSpeed(180, 350));
        enemyBall.addToGame(this);
        this.enemyB.add(enemyBall);
    }
    /**
     * this method informs whn to stop.
     * @return if we should stop
     */
    public boolean shouldStop() {
        return !this.running;
    }
}