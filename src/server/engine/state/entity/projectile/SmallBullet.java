package server.engine.state.entity.projectile;

import server.engine.state.map.tile.Tile;
import shared.Pose;
import shared.lists.EntityList;
import shared.lists.Teams;

public class SmallBullet extends Projectile {
    public static final int DEFAULT_SPEED = Tile.TILE_SIZE * 10;
    public static final int DEFAULT_DAMAGE = 1;
    public static final int DEFAULT_SIZE = 1;
    public static final int DEFAULT_RANGE = 0;

    public SmallBullet(Pose pose, Teams team) {
        this(DEFAULT_SPEED, DEFAULT_DAMAGE, pose, DEFAULT_SIZE, DEFAULT_RANGE, team);
    }

    public SmallBullet(int speed, int damage, Pose pose, int size, int range, Teams team) {
        super(speed, damage, EntityList.BASIC_BULLET, pose, size, range, team);
    }

}
