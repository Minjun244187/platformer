package gamelogic.tiles;

import java.awt.image.BufferedImage;

import gameengine.hitbox.RectHitbox;
import gamelogic.level.Level;

public class Tree extends Tile{

    public Tree(float x, float y, int size, BufferedImage image, Level level) {
        super(x, y, size, image, false, level);

    }


}