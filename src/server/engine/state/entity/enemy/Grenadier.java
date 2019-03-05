package server.engine.state.entity.enemy;

import server.engine.ai.EnemyAI;
import server.engine.ai.GrenadierAI;
import server.engine.ai.SoldierZombieAI;
import server.engine.state.item.weapon.gun.Ammo;
import server.engine.state.map.tile.Tile;
import shared.lists.AmmoList;
import shared.lists.EntityList;

import java.util.LinkedHashSet;

public class Grenadier extends SoldierZombie {

    public static final int DEFAULT_HEALTH = 3;
    public static final int DEFAULT_SCORE_ON_KILL = 50;
    public static final int DEFAULT_MOVESPEED = (Tile.TILE_SIZE / 3);
    public static final int DEFAULT_SIZE = Tile.TILE_SIZE / 2;
    public static final LinkedHashSet<Drop> DEFAULT_DROPS = new LinkedHashSet<>();
    private int rangeToShoot;
    private int rateOfFire;

    static {
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.BASIC_AMMO), 0.4, 8, 4));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.SHOTGUN_ROUND), 0.2, 4, 2));
    }

    //For normal creation
    public Grenadier(int range_to_shoot, int rate_of_fire) {
        super(DEFAULT_HEALTH, DEFAULT_MOVESPEED, EntityList.GRENADIER, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL, new GrenadierAI(range_to_shoot, rate_of_fire));

        this.rangeToShoot = range_to_shoot;
        this.rateOfFire = rate_of_fire;
    }

    //For copying
    private Grenadier(int maxHealth, int moveSpeed, EntityList entity, int size, LinkedHashSet<Drop> drops, int scoreOnKill, EnemyAI ai) {
        super(maxHealth, moveSpeed, entity, size, drops, scoreOnKill, ai);
    }

    //Not sure why we're using this constructor
    @Override
    public Enemy makeCopy() {
        return new Grenadier(this.maxHealth, this.moveSpeed, EntityList.GRENADIER, this.size, this.drops, this.scoreOnKill, new SoldierZombieAI(rangeToShoot, rateOfFire));
    }

}
