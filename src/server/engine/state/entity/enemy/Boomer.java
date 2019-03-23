package server.engine.state.entity.enemy;

import java.util.LinkedHashSet;

import server.engine.ai.BoomerAI;
import server.engine.ai.EnemyAI;
import server.engine.state.item.weapon.gun.Ammo;
import shared.lists.AmmoList;
import shared.lists.EntityList;

public class Boomer extends  Zombie{
    public static final int DEFAULT_HEALTH = 5;
    public static final double DEFAULT_MOVEMENT_FORCE = 1;
    public static final int DEFAULT_SIZE = EntityList.ZOMBIE.getSize();
    public static final int DEFAULT_SCORE_ON_KILL = 50;
    public static final double DEFAULT_MASS = 5;
    public static final LinkedHashSet<Drop> DEFAULT_DROPS = new LinkedHashSet<>();

    static {
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.BASIC_AMMO), 0.4, 4, 2));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.SHOTGUN_ROUND), 0.05, 2, 1));
    }

    public Boomer() {
        super(EntityList.BOOMER, DEFAULT_HEALTH, DEFAULT_MOVEMENT_FORCE, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL, new BoomerAI(), DEFAULT_MASS);
    }

    @Override
    EnemyAI getNewAI() {
        return new BoomerAI();
    }

}
