package server.engine.state.entity.projectile;

import server.engine.state.ContainsAttack;
import server.engine.state.entity.Entity;
import server.engine.state.entity.LivingEntity;
import server.engine.state.entity.attack.AoeAttack;
import server.engine.state.entity.attack.Attack;
import server.engine.state.map.tile.Tile;
import shared.Location;
import shared.Pose;
import shared.lists.EntityList;
import shared.lists.Team;

public class LiveGrenade extends Projectile implements ContainsAttack {
    public static final int DEFAULT_SPEED = Tile.TILE_SIZE * 16;
    public static final int DEFAULT_DAMAGE = 0;
    public static final int DEFAULT_SIZE = EntityList.GRENADE.getSize() / 2;
    public static final int DEFAULT_RANGE = Tile.TILE_SIZE * 12;
    public static final long DEFAULT_FUSE_TIME = 3000;

    protected long creationTime;
    protected long fuseTime;

    public LiveGrenade() {
        this(DEFAULT_SPEED, DEFAULT_DAMAGE, DEFAULT_SIZE, DEFAULT_RANGE, DEFAULT_FUSE_TIME);
    }

    public LiveGrenade(int speed, int damage, int size, int range, long fuseTime) {
        super(speed, damage, EntityList.GRENADE, size, range);
        this.creationTime = System.currentTimeMillis();
        this.fuseTime = fuseTime;
    }

    public LiveGrenade(int speed, int damage, int size, int range, long fuseTime, Pose p, Team team) {
        super(speed, damage, EntityList.GRENADE, size, range, p, team);
        this.creationTime = System.currentTimeMillis();
        this.fuseTime = fuseTime;
    }

    @Override
    public Projectile createFor(Pose p, Team team) {
        return new LiveGrenade(this.speed, this.damage, this.size, this.max_range, this.fuseTime, p, team);
    }

    @Override
    public Entity makeCopy() {
        return new LiveGrenade(speed, damage, size, max_range, fuseTime, pose, team);
    }

    @Override
    public boolean isRemoved() {
        return ((System.currentTimeMillis() - creationTime) > fuseTime);
    }

    @Override
    public boolean isRemoved(Tile tile, Location tileLocation) {
        this.speed = 0;
        return false;
    }

    @Override
    public boolean isRemoved(LivingEntity entity) {
        return false;
    }

    @Override
    public Attack getAttack() {
        return new AoeAttack(pose, Tile.TILE_SIZE * 3, 3, Team.NONE);
    }

}
