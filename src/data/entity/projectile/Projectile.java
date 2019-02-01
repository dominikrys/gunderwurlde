package data.entity.projectile;

import data.Pose;
import data.entity.Entity;

public abstract class Projectile extends Entity {
    protected int speed;
    protected int damage;
    protected ProjectileList projectileType;
    protected int max_range;
    protected int dist_travelled;

    Projectile(int speed, int damage, ProjectileList projectileType, Pose pose, int size, int max_range) {
        super(pose, size);
        this.speed = speed;
        this.damage = damage;
        this.projectileType = projectileType;
        this.max_range = max_range;
        this.dist_travelled = 0;
    }
    
    public boolean maxRangeReached(int distance) {
        this.dist_travelled+=distance;
        if (this.max_range == 0 || this.dist_travelled < max_range) return false;
        else return true;
    }

    public int getRange() {
        return max_range;
    }

    public void setRange(int range) {
        if (range < 0) range = 0; //0 is considered infinite
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

    public ProjectileList getProjectileType() {
        return projectileType;
    }

    public void setProjectileType(ProjectileList projectileType) {
        this.projectileType = projectileType;
    }

}
