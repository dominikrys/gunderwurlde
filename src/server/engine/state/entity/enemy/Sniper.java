package server.engine.state.entity.enemy;

import server.engine.ai.enemyAI.EnemyAI;
import server.engine.ai.enemyAI.SniperAI;
import server.engine.state.item.weapon.gun.Ammo;
import server.engine.state.map.tile.Tile;
import shared.lists.AmmoList;
import shared.lists.EntityList;

import java.util.LinkedHashSet;

public class Sniper extends Zombie {

    public static final int DEFAULT_HEALTH = 1;
    public static final double DEFAULT_ACCELERATION = Tile.TILE_SIZE * 0.98;
    public static final int DEFAULT_SCORE_ON_KILL = 35;
    public static final LinkedHashSet<Drop> DEFAULT_DROPS = new LinkedHashSet<>();

    static {
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.BASIC_AMMO), 0.4, 4, 2));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.SHOTGUN_ROUND), 0.05, 2, 1));
    }

    private final int RANGE_TO_RUN_WAY;

    public Sniper(int rangeToRunAway) {
        super(EntityList.BOOMER, DEFAULT_HEALTH, DEFAULT_ACCELERATION, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL, new SniperAI(rangeToRunAway), DEFAULT_MASS);

        this.RANGE_TO_RUN_WAY = rangeToRunAway;
    }

    @Override
    EnemyAI getNewAI() {
        return new SniperAI(RANGE_TO_RUN_WAY);
    }
}
