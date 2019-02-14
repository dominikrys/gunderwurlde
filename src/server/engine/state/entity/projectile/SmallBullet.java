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

    public SmallBullet() {
        this(DEFAULT_SPEED, DEFAULT_DAMAGE, DEFAULT_SIZE, DEFAULT_RANGE);
    }

    public SmallBullet(int speed, int damage, int size, int range) {
        super(speed, damage, EntityList.BASIC_BULLET, size, range);
    }

    public SmallBullet(int speed, int damage, int size, int range, Pose p, Teams team) {
        super(speed, damage, EntityList.BASIC_BULLET, size, range, p, team);
    }

    @Override
    public Projectile createFor(Pose p, Teams team) {
        return new SmallBullet(this.speed, this.damage, this.size, this.max_range, p, team);
    }

}
