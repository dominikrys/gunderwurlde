package server.engine.state.entity.enemy;


import java.util.LinkedHashSet;

import server.engine.ai.enemyAI.ZombieAI;
import server.engine.state.item.pickup.Ammo;
import server.engine.state.item.pickup.Health;
import server.engine.state.item.weapon.gun.PlasmaPistol;
import shared.lists.AmmoList;
import shared.lists.EntityList;

public class RunnerZombie extends Zombie {

    public static final int DEFAULT_HEALTH = 1;
    public static final int DEFAULT_SCORE_ON_KILL = 15;
    public static final double DEFAULT_MASS = 1;
    public static final LinkedHashSet<Drop> DEFAULT_DROPS = new LinkedHashSet<>();

    static {
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.BASIC_AMMO), 0.2, 4, 2));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.SHOTGUN_ROUND), 0.1, 2, 1));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.HEAVY_AMMO), 0.01, 1, 1));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.ENERGY), 0.2, 12, 4));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.ENERGY), 0.2, 12, 4));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.MAGIC_ESSENCE), 0.1, 1, 1));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.ROCKET_AMMO), 0.01, 1, 1));
        DEFAULT_DROPS.add(new Drop(Health.makeHealth(1), 0.03, 1));
        DEFAULT_DROPS.add(new Drop(new PlasmaPistol(), 0.1, 1, 1));
    }

    public RunnerZombie(int speed){
        super(EntityList.RUNNER, DEFAULT_HEALTH, DEFAULT_MOVEMENT_FORCE * speed, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL, new ZombieAI(),
                DEFAULT_MASS);
    }

}
