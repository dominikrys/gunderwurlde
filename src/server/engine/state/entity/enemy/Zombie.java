package server.engine.state.entity.enemy;

import java.util.LinkedHashSet;

import server.engine.ai.EnemyAI;
import server.engine.ai.ZombieAI;
import server.engine.state.item.weapon.gun.Ammo;
import shared.lists.AmmoList;
import shared.lists.EntityList;

public class Zombie extends Enemy {
    public static final int DEFAULT_HEALTH = 2;
    public static final double DEFAULT_MOVEMENT_FORCE = 2;
    public static final int DEFAULT_SIZE = EntityList.ZOMBIE.getSize() / 2;
    public static final int DEFAULT_SCORE_ON_KILL = 10;
    public static final double DEFAULT_MASS = 2;
    public static final LinkedHashSet<Drop> DEFAULT_DROPS = new LinkedHashSet<>();

    static {
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.BASIC_AMMO), 0.4, 4, 2));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.SHOTGUN_ROUND), 0.05, 2, 1));
    }

    public Zombie() {
        this(EntityList.ZOMBIE, DEFAULT_HEALTH, DEFAULT_MOVEMENT_FORCE, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL, new ZombieAI(), DEFAULT_MASS);
    }

    Zombie(EntityList enemyType, int maxHealth, double acceleration, int size, LinkedHashSet<Drop> drops, int scoreOnKill, EnemyAI ai, double mass) {
        super(maxHealth, acceleration, enemyType, size, drops, scoreOnKill, ai, mass);
    }

    @Override
    EnemyAI getNewAI() {
        return new ZombieAI();
    }

}
