package server.engine.state.entity.projectile;

import java.util.LinkedList;

import server.engine.state.entity.Entity;
import server.engine.state.map.tile.Tile;
import shared.Pose;
import shared.lists.EntityList;
import shared.lists.Team;

public class CrystalBullet extends Projectile {
    public static final int DEFAULT_SPEED = Tile.TILE_SIZE * 22;
    public static final int DEFAULT_DAMAGE = 1;
    public static final int DEFAULT_SIZE = EntityList.CRYSTAL.getSize() / 2;
    public static final int DEFAULT_RANGE = 0;
    public static final int DEFAULT_NUMBER_OF_SPLITS = 1;
    public static final int DEFAULT_AMOUNT_PER_SPLIT = 8;

    private final int numberOfSplits;
    private final int amountPerSplit;

    public CrystalBullet() {
        this(DEFAULT_SPEED, DEFAULT_DAMAGE, DEFAULT_SIZE, DEFAULT_RANGE, DEFAULT_NUMBER_OF_SPLITS, DEFAULT_AMOUNT_PER_SPLIT);
    }

    public CrystalBullet(int speed, int damage, int size, int range, int numOfSplits, int amountPerSplit) {
        super(speed, damage, EntityList.CRYSTAL, size, range);
        this.numberOfSplits = numOfSplits;
        this.amountPerSplit = amountPerSplit;
    }

    public CrystalBullet(int speed, int damage, int size, int range, int numOfSplits, int amountPerSplit, Pose p, Team team) {
        super(speed, damage, EntityList.CRYSTAL, size, range, p, team);
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

    public LinkedList<Projectile> getSplitProjectiles() {
        LinkedList<Projectile> newProjectiles = new LinkedList<>();
        // TODO
        return newProjectiles;
    }

}
