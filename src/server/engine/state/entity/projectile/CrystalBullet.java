package server.engine.state.entity.projectile;

import java.util.LinkedList;

import server.engine.state.ContainsAttack;
import server.engine.state.entity.Entity;
import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.map.tile.Tile;
import shared.Location;
import shared.Pose;
import shared.lists.EntityList;
import shared.lists.Team;

/**
 * 
 * @author Richard
 *
 */
public class CrystalBullet extends Projectile implements ContainsAttack {
    public static final int DEFAULT_SPEED = Tile.TILE_SIZE * 22;
    public static final int DEFAULT_DAMAGE = 1;
    public static final int DEFAULT_SIZE = EntityList.CRYSTAL_BULLET.getSize() / 2;
    public static final int DEFAULT_RANGE = 0;
    public static final int DEFAULT_NUMBER_OF_SPLITS = 1;
    public static final int DEFAULT_AMOUNT_PER_SPLIT = 8;

    private final int amountPerSplit;

    private int numberOfSplits;
    private int ticksPassed = 0;
    private Location lastLocation;

    public CrystalBullet() {
        this(DEFAULT_SPEED, DEFAULT_DAMAGE, DEFAULT_SIZE, DEFAULT_RANGE, DEFAULT_NUMBER_OF_SPLITS, DEFAULT_AMOUNT_PER_SPLIT);
    }

    public CrystalBullet(int speed, int damage, int size, int range, int numOfSplits, int amountPerSplit) {
        super(speed, damage, EntityList.CRYSTAL_BULLET, size, range);
        this.numberOfSplits = numOfSplits;
        this.amountPerSplit = amountPerSplit;
    }

    public CrystalBullet(int speed, int damage, int size, int range, int numOfSplits, int amountPerSplit, Pose p, Team team) {
        super(speed, damage, EntityList.CRYSTAL_BULLET, size, range, p, team);
        this.numberOfSplits = numOfSplits;
        this.amountPerSplit = amountPerSplit;
    }

    @Override
    public Projectile createFor(Pose p, Team team) {
        return new CrystalBullet(this.speed, this.damage, this.size, this.max_range, this.numberOfSplits, this.amountPerSplit, p, team);
    }

    @Override
    public Entity makeCopy() {
        return new CrystalBullet(speed, damage, size, max_range, numberOfSplits, amountPerSplit, pose, team);
    }

    @Override
    public void setLocation(Location location) {
        ticksPassed++;
        lastLocation = this.getLocation();
        super.setLocation(location);
    }

    @Override
    public Attack getAttack() {
        return new ProjectileAttack(getSplitProjectiles());
    }

    private LinkedList<Projectile> getSplitProjectiles() {
        LinkedList<Projectile> newProjectiles = new LinkedList<>();

        if (numberOfSplits > 0 && ticksPassed > 2) {
            int directionGap = 360 / amountPerSplit;
            for (int n = 0; n < amountPerSplit; n++) {
                Projectile projectileToAdd = (CrystalBullet) this.makeCopy();
                ((CrystalBullet) projectileToAdd).numberOfSplits--;
                projectileToAdd.setPose(new Pose(this.lastLocation, this.pose.getDirection() + (directionGap * n)));
                newProjectiles.add(projectileToAdd);
            }
        }

        return newProjectiles;
    }

}
