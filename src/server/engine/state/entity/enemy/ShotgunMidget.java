package server.engine.state.entity.enemy;

import java.util.LinkedHashSet;

import server.engine.ai.enemyAI.EnemyAI;
import server.engine.ai.enemyAI.ShotgunMidgetAI;
import server.engine.state.item.pickup.Ammo;
import server.engine.state.item.pickup.Health;
import shared.lists.AmmoList;
import shared.lists.EntityList;

public class ShotgunMidget extends Zombie {
    public static final int DEFAULT_HEALTH = 1;
    public static final double DEFAULT_MOVEMENT_FORCE = 6;
    public static final int DEFAULT_SCORE_ON_KILL = 15;
    public static final double DEFAULT_MASS = 1;
    public static final LinkedHashSet<Drop> DEFAULT_DROPS = new LinkedHashSet<>();

    private int knockbackAmount;

    static {
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.BASIC_AMMO), 0.2, 2, 1));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.SHOTGUN_ROUND), 0.5, 5, 2));
        DEFAULT_DROPS.add(new Drop(Health.makeHealth(1), 0.1, 1));
    }

    public ShotgunMidget(int speed, int knockbackAmount) {
        super(EntityList.MIDGET, DEFAULT_HEALTH, DEFAULT_MOVEMENT_FORCE * speed, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL,
                new ShotgunMidgetAI(knockbackAmount),
                DEFAULT_MASS);

        this.knockbackAmount = knockbackAmount;
    }


    @Override
    EnemyAI getNewAI() {
        return new ShotgunMidgetAI(knockbackAmount);
    }

}
