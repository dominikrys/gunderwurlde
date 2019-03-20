package server.engine.state.entity.projectile;

import server.engine.state.entity.Entity;
import server.engine.state.map.tile.Tile;
import shared.Pose;
import shared.lists.EntityList;
import shared.lists.Team;

public class BouncyBullet extends Projectile {
    public static final int DEFAULT_SPEED = Tile.TILE_SIZE * 22;
    public static final int DEFAULT_DAMAGE = 1;
    public static final int DEFAULT_SIZE = EntityList.BOUNCE.getSize() / 2;
    public static final int DEFAULT_RANGE = 0;

    public BouncyBullet() {
        this(DEFAULT_SPEED, DEFAULT_DAMAGE, DEFAULT_SIZE, DEFAULT_RANGE);
    }

    public BouncyBullet(int speed, int damage, int size, int range) {
        super(speed, damage, EntityList.BOUNCE, size, range);
    }

    public BouncyBullet(int speed, int damage, int size, int range, Pose p, Team team) {
        super(speed, damage, EntityList.BOUNCE, size, range, p, team);
    }

    @Override
    public Projectile createFor(Pose p, Team team) {
        return new BouncyBullet(this.speed, this.damage, this.size, this.max_range, p, team);
    }

    @Override
    public Entity makeCopy() {
        return new BouncyBullet(speed, damage, size, max_range, pose, team);
    }

}
