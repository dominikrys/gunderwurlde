package server.engine.state.entity.enemy;

import java.util.LinkedHashSet;

import server.engine.ai.EnemyAI;
import server.engine.ai.ZombieAI;
import server.engine.state.item.weapon.gun.Ammo;
import server.engine.state.map.tile.Tile;
import shared.lists.AmmoList;
import shared.lists.EntityList;

public class Zombie extends Enemy {
    public static final int DEFAULT_HEALTH = 2;
    public static final int DEFAULT_MOVESPEED = (Tile.TILE_SIZE / 3) * 2;
    public static final int DEFAULT_SIZE = Tile.TILE_SIZE / 2;
    public static final int DEFAULT_SCORE_ON_KILL = 10;
    public static final LinkedHashSet<Drop> DEFAULT_DROPS = new LinkedHashSet<>();

    static {
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.BASIC_AMMO), 0.4, 4, 2));
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.SHOTGUN_ROUND), 0.05, 2, 1));
    }

    public Zombie() {
        this(DEFAULT_HEALTH, DEFAULT_MOVESPEED, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL, new ZombieAI());
    }

    Zombie(int maxHealth, int moveSpeed, int size, LinkedHashSet<Drop> drops, int scoreOnKill, EnemyAI ai) {
        super(maxHealth, moveSpeed, EntityList.ZOMBIE, size, drops, scoreOnKill, ai);
    }

    Zombie(int maxHealth, int moveSpeed, int size, LinkedHashSet<Drop> drops, int scoreOnKill, EnemyAI ai, EntityList type) {
        super(maxHealth, moveSpeed, type, size, drops, scoreOnKill, ai);
    }

    @Override
    public Enemy makeCopy() {
        return new Zombie(this.maxHealth, this.moveSpeed, this.size, this.drops, this.scoreOnKill, new ZombieAI());
    }

}
