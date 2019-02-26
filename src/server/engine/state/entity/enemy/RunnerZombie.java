package server.engine.state.entity.enemy;

import server.engine.ai.ZombieAI;
import server.engine.state.map.tile.Tile;
import shared.lists.EntityList;

public class RunnerZombie extends Zombie {

    public static final int DEFAULT_HEALTH = 1;
    public static final int DEFAULT_MOVESPEED = Tile.TILE_SIZE;
    public static final int DEFAULT_SCORE_ON_KILL = 15;

    public RunnerZombie(int speed){
        super(DEFAULT_HEALTH, DEFAULT_MOVESPEED * speed, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL, new ZombieAI(), EntityList.RUNNER);
    }

}
