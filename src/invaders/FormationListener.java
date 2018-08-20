package invaders;

/**
 * Created by shaytzir on 27/06/2017.
 */
public interface FormationListener {
    /**
     * if enemeies collide shield.
     */
    void collideShield();

    /**
     * if all enemies in formation died.
     */
    void deadEnemies();
}
