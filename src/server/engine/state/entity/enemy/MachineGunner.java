package server.engine.state.entity.enemy;

import java.util.LinkedHashSet;

import server.engine.ai.enemyAI.EnemyAI;
import server.engine.ai.enemyAI.MachineGunnerAI;
import server.engine.state.item.pickup.Ammo;
import server.engine.state.item.pickup.Health;
import shared.lists.AmmoList;
import shared.lists.EntityList;

public class MachineGunner extends Zombie {
    public static final int DEFAULT_HEALTH = 15;

    public static final double DEFAULT_MOVEMENT_FORCE = 2;
    public static final int DEFAULT_SIZE = EntityList.ZOMBIE.getSize()/2;
    public static final int DEFAULT_SCORE_ON_KILL = 100;
    public static final double DEFAULT_MASS = 10;
    public static final LinkedHashSet<Drop> DEFAULT_DROPS = new LinkedHashSet<>();

    //TODO think about this
    static {
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.BASIC_AMMO), 0.2, 4, 2));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.HEAVY_AMMO), 0.6, 3, 1));
        DEFAULT_DROPS.add(new Drop(Health.makeHealth(2), 0.05, 2));
        DEFAULT_DROPS.add(new Drop(Health.makeHealth(1), 0.3, 1));
    }

    private final int ATTACK_WIDTH;
    private final int BULLETS_PER_ATTACK;
    private final int TURN_RATE;
    private final int DISTANCE_TO_PLAYER_TO_ATTACK;

    public MachineGunner(int attackWidth, int bulletsPerAttack, int turnRate, int distanceToPlayerToAttack) {
        super(EntityList.MACHINE_GUNNER, DEFAULT_HEALTH, DEFAULT_MOVEMENT_FORCE, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL,
                new MachineGunnerAI(attackWidth, bulletsPerAttack, turnRate, distanceToPlayerToAttack), DEFAULT_MASS);

        this.ATTACK_WIDTH = attackWidth;
        this.BULLETS_PER_ATTACK = bulletsPerAttack;
        this.TURN_RATE = turnRate;
        this.DISTANCE_TO_PLAYER_TO_ATTACK = distanceToPlayerToAttack;
    }

    @Override
    EnemyAI getNewAI() {
        return new MachineGunnerAI(
                ATTACK_WIDTH, BULLETS_PER_ATTACK,
                TURN_RATE, DISTANCE_TO_PLAYER_TO_ATTACK);
    }
}
