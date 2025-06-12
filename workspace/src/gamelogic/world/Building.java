package gamelogic.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import gameengine.GameObject;
import gameengine.hitbox.RectHitbox;
import gameengine.maths.Vector2D;

public class Building {
    private Vector2D position;
    private int width, height;
    private BufferedImage image;
    private boolean solid = true;
    private boolean collidable = true; // FIX: was missing

    protected RectHitbox hitbox;

    public Building(float x, float y, int width, int height, BufferedImage image) {
        this.position = new Vector2D(x * width, y * height);
        this.width = width;
        this.height = height;
        this.image = image;
        this.hitbox = new RectHitbox(position.x, position.y, 0 , 0, width, height); // No parent object
    }

    public void draw(Graphics g, float camX, float camY) {
        g.drawImage(image, (int)position.x, (int)position.y, width, height, null);
        if (hitbox != null) hitbox.draw(g);
    }

    public boolean isSolid() {
        return solid;
    }

    public boolean isCollidable() {
        return collidable;
    }

    public RectHitbox getHitbox() {
        return hitbox;
    }

    public Vector2D getPosition() {
        return position;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean intersects(Vector2D point, int w, int h) {
        return point.x + w > position.x && point.x < position.x + width &&
                point.y + h > position.y && point.y < position.y + height;
    }

    // Optional: helper for logging/debug
    @Override
    public String toString() {
        return "Building[" + position.x + "," + position.y + "]";
    }
}