package server.engine.state.entity.enemy;

import java.util.LinkedHashSet;

import server.engine.ai.enemyAI.EnemyAI;
import server.engine.ai.enemyAI.MageAI;
import server.engine.state.item.pickup.Ammo;
import server.engine.state.item.pickup.Health;
import shared.lists.AmmoList;
import shared.lists.EntityList;

public class Mage extends Zombie{
    public static final int DEFAULT_HEALTH = 10;
    public static final double DEFAULT_MOVEMENT_FORCE = 2;
    public static final int DEFAULT_SCORE_ON_KILL = 100;
    public static final LinkedHashSet<Drop> DEFAULT_DROPS = new LinkedHashSet<>();

    static {
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.BASIC_AMMO), 0.4, 4, 2));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.SHOTGUN_ROUND), 0.05, 2, 1));
        DEFAULT_DROPS.add(new Drop(Health.makeHealth(1), 0.01, 1));
    }

    private final long TIME_BETWEEN_TELEPORTS;
    private final int DISTANCE_TO_PLAYER;

    public Mage(long timeBetweenTeleports, int distanceToPlayer) {
        super(EntityList.BOOMER, DEFAULT_HEALTH, DEFAULT_MOVEMENT_FORCE, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL, new MageAI(timeBetweenTeleports, distanceToPlayer),
                DEFAULT_MASS);

        this.TIME_BETWEEN_TELEPORTS = timeBetweenTeleports;
        this.DISTANCE_TO_PLAYER = distanceToPlayer;
    }

    @Override
    EnemyAI getNewAI() {
        return new MageAI(TIME_BETWEEN_TELEPORTS, DISTANCE_TO_PLAYER);
    }
}
