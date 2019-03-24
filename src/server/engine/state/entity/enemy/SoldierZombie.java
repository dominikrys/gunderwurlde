package server.engine.state.entity.enemy;


import java.util.LinkedHashSet;

import server.engine.ai.enemyAI.EnemyAI;
import server.engine.ai.enemyAI.SoldierZombieAI;
import server.engine.state.item.pickup.Ammo;
import server.engine.state.item.pickup.Health;
import shared.lists.AmmoList;
import shared.lists.EntityList;

public class SoldierZombie extends Zombie {

    public static final int DEFAULT_SCORE_ON_KILL = 30;
    public static final double DEFAULT_MOVEMENT_FORCE = 3;
    public static final LinkedHashSet<Drop> DEFAULT_DROPS = new LinkedHashSet<>();

    private int rangeToShoot;
    private int rateOfFire;

    static {
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.BASIC_AMMO), 0.5, 4, 2));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.SHOTGUN_ROUND), 0.2, 3, 1));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.HEAVY_AMMO), 0.1, 2, 1));
        DEFAULT_DROPS.add(new Drop(Health.makeHealth(2), 0.01, 2));
        DEFAULT_DROPS.add(new Drop(Health.makeHealth(1), 0.06, 1));
    }

    public SoldierZombie(int range_to_shoot, int rate_of_fire) {
        super(EntityList.SOLDIER, DEFAULT_HEALTH, DEFAULT_MOVEMENT_FORCE, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL,
                new SoldierZombieAI(range_to_shoot, rate_of_fire),
                DEFAULT_MASS);

        this.rangeToShoot = range_to_shoot;
        this.rateOfFire = rate_of_fire;
    }

    @Override
    EnemyAI getNewAI() {
        return new SoldierZombieAI(rangeToShoot, rateOfFire);
    }

}
