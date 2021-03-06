package server.engine.state.entity.projectile;

import server.engine.state.entity.Entity;
import server.engine.state.map.tile.Tile;
import shared.Pose;
import shared.lists.EntityList;
import shared.lists.Team;

/**
 * 
 * @author Richard
 *
 */
public class HeavyBullet extends Projectile {
    public static final int DEFAULT_SPEED = Tile.TILE_SIZE * 20;
    public static final int DEFAULT_DAMAGE = 2;
    public static final int DEFAULT_SIZE = EntityList.HEAVY_BULLET.getSize() / 2;
    public static final int DEFAULT_RANGE = 0;

    public HeavyBullet() {
        this(DEFAULT_SPEED, DEFAULT_DAMAGE, DEFAULT_SIZE, DEFAULT_RANGE);
    }

    public HeavyBullet(int speed, int damage, int size, int range) {
        super(speed, damage, EntityList.HEAVY_BULLET, size, range);
    }

    public HeavyBullet(int speed, int damage, int size, int range, Pose p, Team team) {
        super(speed, damage, EntityList.HEAVY_BULLET, size, range, p, team);
    }

    @Override
    public Projectile createFor(Pose p, Team team) {
        return new HeavyBullet(this.speed, this.damage, this.size, this.max_range, p, team);
    }

    @Override
    public Entity makeCopy() {
        return new HeavyBullet(speed, damage, size, max_range, pose, team);
    }

}
