package server.engine.state.entity.projectile;

import server.engine.state.ContainsAttack;
import server.engine.state.entity.Entity;
import server.engine.state.entity.attack.AoeAttack;
import server.engine.state.entity.attack.Attack;
import server.engine.state.map.tile.Tile;
import shared.Pose;
import shared.lists.EntityList;
import shared.lists.Team;

public class Rocket extends Projectile implements ContainsAttack {
    public static final int DEFAULT_SPEED = Tile.TILE_SIZE * 32;
    public static final int DEFAULT_DAMAGE = 1;
    public static final int DEFAULT_SIZE = EntityList.ROCKET.getSize() / 2;
    public static final int DEFAULT_RANGE = 0;

    public Rocket() {
        this(DEFAULT_SPEED, DEFAULT_DAMAGE, DEFAULT_SIZE, DEFAULT_RANGE);
    }

    public Rocket(int speed, int damage, int size, int range) {
        super(speed, damage, EntityList.ROCKET, size, range);
    }

    public Rocket(int speed, int damage, int size, int range, Pose p, Team team) {
        super(speed, damage, EntityList.ROCKET, size, range, p, team);
    }

    @Override
    public Projectile createFor(Pose p, Team team) {
        return new Rocket(this.speed, this.damage, this.size, this.max_range, p, team);
    }

    @Override
    public Entity makeCopy() {
        return new Rocket(speed, damage, size, max_range, pose, team);
    }

    @Override
    public Attack getAttack() {
        return new AoeAttack(pose, Tile.TILE_SIZE * 2, 4, Team.NONE);
    }

}
