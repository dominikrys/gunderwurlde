package server.engine.state.entity.projectile;

import server.engine.state.entity.Entity;
import server.engine.state.entity.LivingEntity;
import server.engine.state.map.tile.Tile;
import server.engine.state.physics.Force;
import shared.Location;
import shared.Pose;
import shared.lists.EntityList;
import shared.lists.Team;

public abstract class Projectile extends Entity {
    private static final int FORCE_PER_DMG = 300;

    protected int speed; // max ~1800
    protected int damage;
    protected EntityList entityListName;
    protected int max_range;
    protected int dist_travelled;
    protected Team team;

    Projectile(int speed, int damage, EntityList entityListName, int size, int max_range, Pose pose, Team team) {
        super(pose, size + 1, entityListName);
        this.speed = speed;
        this.damage = damage;
        this.max_range = max_range;
        this.dist_travelled = 0;
        this.team = team;
    }

    Projectile(int speed, int damage, EntityList entityListName, int size, int max_range) {
        super(size, entityListName);
        this.speed = speed;
        this.damage = damage;
        this.max_range = max_range;
        this.dist_travelled = 0;
        this.team = Team.NONE;
    }

    public Team getTeam() {
        return team;
    }

    public boolean maxRangeReached(double distanceMoved) {
        this.dist_travelled += distanceMoved;
        if (this.max_range == 0 || this.dist_travelled < max_range)
            return false;
        else
            return true;
    }

    public int getRange() {
        return max_range;
    }

    public void setRange(int range) {
        if (range < 0)
            range = 0; // 0 is considered infinite
        this.max_range = range;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public abstract Projectile createFor(Pose p, Team team);

    public Force getImpactForce() {
        return new Force(pose.getDirection(), Math.pow(damage, 1.2) * FORCE_PER_DMG);
    }

    public boolean isRemoved() {
        return false;
    }

    public boolean isRemoved(Tile tile, Location tileLocation) {
        return true;
    }

    public boolean isRemoved(LivingEntity entity) {
        return true;
    }

}
