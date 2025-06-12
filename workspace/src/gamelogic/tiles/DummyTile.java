package gamelogic.tiles;

import gameengine.hitbox.RectHitbox;

public class DummyTile extends Tile {
    private RectHitbox hitbox;

    public DummyTile(RectHitbox hitbox) {
        super(null, false); // null image, not solid (custom logic)
        this.hitbox = hitbox;
    }

    @Override
    public RectHitbox getHitbox() {
        return hitbox;
    }

    @Override
    public boolean isSolid() {
        return true; // treat as solid
    }
}
