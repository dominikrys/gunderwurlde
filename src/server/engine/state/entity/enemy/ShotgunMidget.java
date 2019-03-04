package server.engine.state.entity.enemy;

import java.util.LinkedHashSet;

import server.engine.ai.EnemyAI;
import server.engine.ai.ShotgunMidgetAI;
import server.engine.state.item.weapon.gun.Ammo;
import server.engine.state.map.tile.Tile;
import shared.lists.AmmoList;
import shared.lists.EntityList;

public class ShotgunMidget extends Enemy {
    public static final int DEFAULT_HEALTH = 1;
    public static final int DEFAULT_MOVESPEED = (Tile.TILE_SIZE / 3);
    public static final int DEFAULT_SIZE = Tile.TILE_SIZE / 2;
    public static final int DEFAULT_SCORE_ON_KILL = 15;
    public static final double DEFAULT_MASS = 1;
    public static final LinkedHashSet<Drop> DEFAULT_DROPS = new LinkedHashSet<>();

    private int knockbackAmount;

    static {
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.BASIC_AMMO), 0.4, 4, 2));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.SHOTGUN_ROUND), 0.05, 2, 1));
    }

    public ShotgunMidget(int speed, int knockbackAmount) {
        this(DEFAULT_HEALTH, DEFAULT_MOVESPEED * speed, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL, new ShotgunMidgetAI(knockbackAmount), DEFAULT_MASS);

        this.knockbackAmount = knockbackAmount;
    }

    ShotgunMidget(int maxHealth, int moveSpeed, int size, LinkedHashSet<Drop> drops, int scoreOnKill, EnemyAI ai, double mass) {
        super(maxHealth, moveSpeed, EntityList.MIDGET, size, drops, scoreOnKill, ai, mass);
    }

    @Override
    public Enemy makeCopy() {
        return new ShotgunMidget(this.maxHealth, this.moveSpeed, this.size, this.drops, this.scoreOnKill, new ShotgunMidgetAI(knockbackAmount), this.mass);
    }
}
