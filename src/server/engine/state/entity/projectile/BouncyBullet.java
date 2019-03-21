package server.engine.state.entity.projectile;

import server.engine.state.entity.Entity;
import server.engine.state.map.tile.Tile;
import server.engine.state.physics.Force;
import server.engine.state.physics.HasPhysics;
import server.engine.state.physics.Physics;
import server.engine.state.physics.Velocity;
import shared.Location;
import shared.Pose;
import shared.lists.EntityList;
import shared.lists.Team;

public class BouncyBullet extends Projectile implements HasPhysics { // TODO find a cleaner way
    public static final int DEFAULT_SPEED = Tile.TILE_SIZE * 22;
    public static final int DEFAULT_DAMAGE = 1;
    public static final int DEFAULT_SIZE = EntityList.BOUNCE.getSize() / 2;
    public static final int DEFAULT_RANGE = 0;
    public static final int DEFAULT_BOUNCE_COUNT = 2;

    private int bouncesLeft;

    public BouncyBullet() {
        this(DEFAULT_SPEED, DEFAULT_DAMAGE, DEFAULT_SIZE, DEFAULT_RANGE, DEFAULT_BOUNCE_COUNT);
    }

    public BouncyBullet(int speed, int damage, int size, int range, int bounces) {
        super(speed, damage, EntityList.BOUNCE, size, range);
        this.bouncesLeft = bounces;
    }

    public BouncyBullet(int speed, int damage, int size, int range, Pose p, Team team, int bounces) {
        super(speed, damage, EntityList.BOUNCE, size, range, p, team);
        this.bouncesLeft = bounces;
    }

    @Override
    public Projectile createFor(Pose p, Team team) {
        return new BouncyBullet(this.speed, this.damage, this.size, this.max_range, p, team, this.bouncesLeft);
    }

    @Override
    public Entity makeCopy() {
        return new BouncyBullet(speed, damage, size, max_range, pose, team, bouncesLeft);
    }

    @Override
    public boolean isRemoved(Tile tile, Location tileLocation) {
        if (--bouncesLeft == 0) {
            return true;
        } else {
            Physics.tileCollision(this, tileLocation, tile.getBounceCoefficient());
            return false;
        }
    }

    @Override
    public Velocity getVelocity() {
        return new Velocity(this.pose.getDirection(), 0);
    }

    @Override
    public void setVelocity(Velocity v) {
        this.pose = new Pose(pose, v.getDirection());
    }

    @Override
    public Force getResultantForce() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addNewForce(Force f) {
        // TODO Auto-generated method stub

    }

    @Override
    public double getMass() {
        // TODO Auto-generated method stub
        return 0;
    }

}
