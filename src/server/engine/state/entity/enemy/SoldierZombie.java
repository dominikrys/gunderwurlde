package server.engine.state.entity.enemy;

import java.util.LinkedHashSet;

import server.engine.ai.EnemyAI;
import server.engine.ai.SoldierZombieAI;
import server.engine.state.item.weapon.gun.Ammo;
import server.engine.state.map.tile.Tile;
import shared.lists.AmmoList;
import shared.lists.EntityList;

public class SoldierZombie extends Enemy {

    public static final int DEFAULT_HEALTH = 2;
    public static final int DEFAULT_SCORE_ON_KILL = 30;
    public static final int DEFAULT_MOVESPEED = (Tile.TILE_SIZE / 3) * 2;
    public static final int DEFAULT_SIZE = Tile.TILE_SIZE / 2;
    public static final LinkedHashSet<Drop> DEFAULT_DROPS = new LinkedHashSet<>();
    private int rangeToShoot;
    private int rateOfFire;

    static {
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.BASIC_AMMO), 0.4, 4, 2));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.SHOTGUN_ROUND), 0.2, 2, 1));
    }

    //For normal creation
    public SoldierZombie(int range_to_shoot, int rate_of_fire){
        super(DEFAULT_HEALTH, DEFAULT_MOVESPEED, EntityList.SOLDIER, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL, new SoldierZombieAI(range_to_shoot, rate_of_fire));
        this.rangeToShoot = range_to_shoot;
        this.rateOfFire = rate_of_fire;
    }
//
//    //For extensions
//    public SoldierZombie(int range_to_shoot, int rate_of_fire, int maxHealth, int moveSpeed, int size, LinkedHashSet<Drop> drops, int scoreOnKill, EnemyAI ai){
//        super(maxHealth, moveSpeed, EntityList.SOLDIER, size, drops, scoreOnKill, ai);
//        this.rangeToShoot = range_to_shoot;
//        this.rateOfFire = rate_of_fire;
//    }

    //For copying
    SoldierZombie(int maxHealth, int moveSpeed, EntityList entity, int size, LinkedHashSet<Drop> drops, int scoreOnKill, EnemyAI ai) {
        super(maxHealth, moveSpeed, entity, size, drops, scoreOnKill, ai);
    }

    @Override
    public Enemy makeCopy() {
        return new SoldierZombie(this.maxHealth, this.moveSpeed, EntityList.SOLDIER, this.size, this.drops, this.scoreOnKill, new SoldierZombieAI(rangeToShoot, rateOfFire));
    }


}
