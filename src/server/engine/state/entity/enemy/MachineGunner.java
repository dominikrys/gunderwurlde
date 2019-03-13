package server.engine.state.entity.enemy;

import server.engine.ai.BoomerAI;
import server.engine.ai.EnemyAI;
import server.engine.ai.MachineGunnerAI;
import server.engine.ai.SoldierZombieAI;
import server.engine.state.item.weapon.gun.Ammo;
import server.engine.state.map.tile.Tile;
import shared.lists.AmmoList;
import shared.lists.EntityList;

import java.util.LinkedHashSet;

public class MachineGunner extends Zombie {
    public static final int DEFAULT_HEALTH = 15;
    public static final double DEFAULT_ACCELERATION = Tile.TILE_SIZE * 0.5;
    public static final int DEFAULT_SIZE = EntityList.ZOMBIE.getSize()/2;
    public static final int DEFAULT_SCORE_ON_KILL = 100;
    public static final double DEFAULT_MASS = 10;
    public static final LinkedHashSet<Drop> DEFAULT_DROPS = new LinkedHashSet<>();

    //TODO think about this
    static {
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.BASIC_AMMO), 0.4, 4, 2));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.SHOTGUN_ROUND), 0.05, 2, 1));
    }

    private final int ATTACK_WIDTH;
    private final int BULLETS_PER_ATTACK;

    public MachineGunner(int attackWidth, int bulletsPerAttack) {
        super(EntityList.MACHINE_GUNNER, DEFAULT_HEALTH, DEFAULT_ACCELERATION, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL, new MachineGunnerAI(attackWidth, bulletsPerAttack), DEFAULT_MASS);

        this.ATTACK_WIDTH = attackWidth;
        this.BULLETS_PER_ATTACK = bulletsPerAttack;
    }

    @Override
    EnemyAI getNewAI() {
        return new MachineGunnerAI(ATTACK_WIDTH, BULLETS_PER_ATTACK);
    }
}
