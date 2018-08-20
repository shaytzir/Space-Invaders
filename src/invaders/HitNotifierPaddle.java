package invaders;


/**
 * Created by shaytzir on 27/06/2017.
 */
public interface HitNotifierPaddle {
    /**
     * add hl as a listener to hit events.
     * @param hl HitListener
     */
    void addHitPListener(HitPListener hl);
    /**
     * remove hl from the list of listeners to hit events.
     * @param hl HitListener
     */
    void removeHitPListener(HitPListener hl);
}
