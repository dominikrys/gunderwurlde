package server.engine.state.entity.enemy;

import server.engine.ai.BoomerAI;
import server.engine.state.item.weapon.gun.Ammo;
import server.engine.state.map.tile.Tile;
import shared.lists.AmmoList;
import shared.lists.EntityList;

import java.util.LinkedHashSet;

public class Boomer extends  Zombie{
    public static final int DEFAULT_HEALTH = 5;
    public static final double DEFAULT_ACCELERATION = Tile.TILE_SIZE * 0.7;
    public static final int DEFAULT_SIZE = EntityList.ZOMBIE.getSize();
    public static final int DEFAULT_SCORE_ON_KILL = 50;
    public static final double DEFAULT_MASS = 5;
    public static final LinkedHashSet<Drop> DEFAULT_DROPS = new LinkedHashSet<>();

    static {
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.BASIC_AMMO), 0.4, 4, 2));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.SHOTGUN_ROUND), 0.05, 2, 1));
    }

    public Boomer() {
        super(EntityList.BOOMER, DEFAULT_HEALTH, DEFAULT_ACCELERATION, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL, new BoomerAI(), DEFAULT_MASS);
    }

}
