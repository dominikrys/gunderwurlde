package server.engine.state.entity.enemy;

import server.engine.ai.RunnerZombieAI;
import server.engine.ai.ZombieAI;
import server.engine.state.map.tile.Tile;

public class RunnerZombie extends Zombie {

    public static final int DEFAULT_HEALTH = 1;
    public static final int DEFAULT_MOVESPEED = Tile.TILE_SIZE * 3;
    public static final int DEFAULT_SCORE_ON_KILL = 15;

    public RunnerZombie(){
        super(DEFAULT_HEALTH, DEFAULT_MOVESPEED, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL, new ZombieAI());
    }

}
