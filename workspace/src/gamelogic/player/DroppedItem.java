package gamelogic.player;

import java.awt.*;

public class DroppedItem {
    private Item item;
    private int x, y;

    public DroppedItem(Item item, int x, int y) {
        this.item = item;
        this.x = x;
        this.y = y;
    }

    public Item getItem() { return item; }

    public int getX() { return x; }

    public int getY() { return y; }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32); // Assume 32x32 icon
    }

    public void render(Graphics g) {
        if (item.getImage() != null) {
            g.drawImage(item.getImage(), x, y, null);
        } else {
            g.setColor(Color.YELLOW);
            g.fillRect(x, y, 32, 32);
        }
    }
}
