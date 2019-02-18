package server.engine.state.entity.enemy;

import server.engine.state.map.tile.Tile;
import java.util.LinkedHashSet;

public class RunnerZombie extends Zombie {

    public static final int DEFAULT_HEALTH = 1;
    public static final int DEFAULT_MOVESPEED = Tile.TILE_SIZE * 4;
    public static final int DEFAULT_SIZE = Tile.TILE_SIZE / 2;
    public static final int DEFAULT_SCORE_ON_KILL = 15;
    public static final LinkedHashSet<Drop> DEFAULT_DROPS = new LinkedHashSet<>();

    public RunnerZombie(){
        super(DEFAULT_HEALTH, DEFAULT_MOVESPEED, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL);
    }

}
