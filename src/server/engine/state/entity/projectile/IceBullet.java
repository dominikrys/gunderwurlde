package server.engine.state.entity.projectile;

import server.engine.state.effect.FreezeEffect;
import server.engine.state.effect.StatusEffect;
import server.engine.state.entity.Entity;
import server.engine.state.map.tile.Tile;
import shared.Pose;
import shared.lists.EntityList;
import shared.lists.Team;

public class IceBullet extends Projectile implements HasEffect {
    public static final int DEFAULT_SPEED = Tile.TILE_SIZE * 18;
    public static final int DEFAULT_DAMAGE = 1;
    public static final int DEFAULT_SIZE = EntityList.ICE_BULLET.getSize() / 2;
    public static final int DEFAULT_RANGE = Tile.TILE_SIZE * 20;

    public IceBullet() {
        this(DEFAULT_SPEED, DEFAULT_DAMAGE, DEFAULT_SIZE, DEFAULT_RANGE);
    }

    public IceBullet(int speed, int damage, int size, int range) {
        super(speed, damage, EntityList.ICE_BULLET, size, range);
    }

    public IceBullet(int speed, int damage, int size, int range, Pose p, Team team) {
        super(speed, damage, EntityList.ICE_BULLET, size, range, p, team);
    }

    @Override
    public Projectile createFor(Pose p, Team team) {
        return new IceBullet(this.speed, this.damage, this.size, this.max_range, p, team);
    }

    @Override
    public Entity makeCopy() {
        return new IceBullet(speed, damage, size, max_range, pose, team);
    }

    @Override
    public StatusEffect getEffect() {
        return new FreezeEffect();
    }

}
