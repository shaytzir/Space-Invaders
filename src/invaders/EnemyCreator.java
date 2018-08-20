package invaders;

import io.Background;
import io.BlockCreator;
import java.io.IOException;
import java.util.TreeMap;

/**
 * Created by shaytzir on 25/06/2017.
 */
public class EnemyCreator implements BlockCreator {
    private int width;
    private int height;
    private TreeMap<String, Background> fillMap;

    /**
     *Constructor.
     */
    public EnemyCreator() {
        this.width = 40;
        this.height = 30;
        this.fillMap = new TreeMap<String, Background>();
        java.awt.image.BufferedImage img = null;
        java.io.InputStream is = null;
        try {
            is = ClassLoader.getSystemClassLoader().getResourceAsStream("definitions/alien.jpg");
            img = javax.imageio.ImageIO.read(is);
        } catch (IOException e) {
            System.out.println("cant load image");
            System.exit(1);
        }
        this.fillMap.put("default", new Background(img));
    }


    @Override
    public Enemy create(int xpos, int ypos) {
        return new Enemy(xpos, ypos, this.fillMap);
    }
}
