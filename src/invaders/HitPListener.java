package invaders;

import arkanoid.Ball;
import arkanoid.Paddle;

/**
 * Created by shaytzir on 27/06/2017.
 */
public interface HitPListener {
    /**
     * this method is called whenever the Paddle object is hit.
     * @param beingHit an object being hit by the hitter
     * @param hitter the ball that hits the "beingHit"
     */
    void hitEvent(Paddle beingHit, Ball hitter);
}
