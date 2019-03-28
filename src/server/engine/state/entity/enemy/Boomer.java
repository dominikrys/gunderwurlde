package server.engine.state.entity.enemy;

import java.util.LinkedHashSet;

import server.engine.ai.enemyAI.BoomerAI;
import server.engine.ai.enemyAI.EnemyAI;
import server.engine.state.item.consumable.Grenade;
import server.engine.state.item.pickup.Ammo;
import server.engine.state.item.pickup.Health;
import server.engine.state.item.weapon.gun.HeavyLaserCannon;
import shared.lists.AmmoList;
import shared.lists.EntityList;

public class Boomer extends  Zombie{

    public static final double DEFAULT_MOVEMENT_FORCE = 1;
    public static final int DEFAULT_SIZE = EntityList.ZOMBIE.getSize();
    public static final int DEFAULT_SCORE_ON_KILL = 50;
    public static final double DEFAULT_MASS = 5;
    public static final LinkedHashSet<Drop> DEFAULT_DROPS = new LinkedHashSet<>();

    static {
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.BASIC_AMMO), 0.6, 4, 2));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.SHOTGUN_ROUND), 0.2, 2, 1));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.HEAVY_AMMO), 0.2, 2, 1));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.ENERGY), 0.05, 16, 4));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.MAGIC_ESSENCE), 0.1, 1, 1));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.ROCKET_AMMO), 0.05, 1, 1));
        DEFAULT_DROPS.add(new Drop(Health.makeHealth(1), 0.6, 1));
        DEFAULT_DROPS.add(new Drop(Health.makeHealth(1), 0.4, 1));
        DEFAULT_DROPS.add(new Drop(Health.makeHealth(2), 0.1, 1));
        DEFAULT_DROPS.add(new Drop(new HeavyLaserCannon(), 0.1, 1, 1));
        DEFAULT_DROPS.add(new Drop(new Grenade(), 0.5, 1, 1));
        DEFAULT_DROPS.add(new Drop(new Grenade(), 0.5, 1, 1));

    }

    private final int DEFAULT_DAMAGE;

    public Boomer(int damage) {
        super(EntityList.BOOMER, damage, DEFAULT_MOVEMENT_FORCE, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL, new BoomerAI(damage), DEFAULT_MASS);

        this.DEFAULT_DAMAGE = damage;
    }

    @Override
    EnemyAI getNewAI() {
        return new BoomerAI(DEFAULT_DAMAGE);
    }

}
