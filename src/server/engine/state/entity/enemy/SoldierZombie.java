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

    private final int RANGE_TO_SHOOT;
    private final int RATE_OF_FIRE;
    private final int DISTANCE_TO_MOVE;

    static {
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.BASIC_AMMO), 0.6, 4, 2));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.SHOTGUN_ROUND), 0.3, 3, 1));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.HEAVY_AMMO), 0.2, 2, 1));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.ENERGY), 0.1, 12, 4));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.ROCKET_AMMO), 0.2, 1, 1));
        DEFAULT_DROPS.add(new Drop(Health.makeHealth(2), 0.06, 2));
        DEFAULT_DROPS.add(new Drop(Health.makeHealth(1), 0.3, 1));
    }

    public SoldierZombie(int range_to_shoot, int rate_of_fire, int distanceToMove) {
        super(EntityList.SOLDIER, DEFAULT_HEALTH, DEFAULT_MOVEMENT_FORCE, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL,
                new SoldierZombieAI(range_to_shoot, rate_of_fire, distanceToMove),
                DEFAULT_MASS);

        this.RANGE_TO_SHOOT = range_to_shoot;
        this.RATE_OF_FIRE = rate_of_fire;
        this.DISTANCE_TO_MOVE = distanceToMove;
    }

    @Override
    EnemyAI getNewAI() {
        return new SoldierZombieAI(RANGE_TO_SHOOT, RATE_OF_FIRE, DISTANCE_TO_MOVE);
    }

}
