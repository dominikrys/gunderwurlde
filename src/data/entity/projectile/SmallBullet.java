package data.entity.projectile;

import data.Pose;
import data.map.tile.Tile;

public class SmallBullet extends Projectile {
    public static final int DEFAULT_SPEED = Tile.TILE_SIZE * 3;
    public static final int DEFAULT_DAMAGE = 1;
    public static final int DEFAULT_SIZE = 1;
    public static final int DEFAULT_RANGE = 0;

    public SmallBullet(Pose pose) {
        this(DEFAULT_SPEED, DEFAULT_DAMAGE, pose, DEFAULT_SIZE, DEFAULT_RANGE);
    }

    public SmallBullet(int speed, int damage, Pose pose, int size, int range) {
        super(speed, damage, ProjectileList.SMALLBULLET, pose, size, range);
    }

}
