package server.game_engine.data;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class GameStateChanges {
    protected final LinkedHashSet<ProjectileChange> projectileChanges;
    protected final LinkedHashSet<EnemyChange> enemyChanges;
    protected final LinkedHashMap<Integer, PlayerChange> playerChanges;
    protected final TileChange[][] tileChanges;

    public GameStateChanges(LinkedHashSet<ProjectileChange> projectileChanges, LinkedHashSet<EnemyChange> enemyChanges,
	    LinkedHashMap<Integer, PlayerChange> playerChanges, TileChange[][] tileChanges) {
	this.projectileChanges = projectileChanges;
	this.enemyChanges = enemyChanges;
	this.playerChanges = playerChanges;
	this.tileChanges = tileChanges;
    }

    public LinkedHashSet<ProjectileChange> getProjectileChanges() {
	return projectileChanges;
    }

    public LinkedHashSet<EnemyChange> getEnemyChanges() {
	return enemyChanges;
    }

    public LinkedHashMap<Integer, PlayerChange> getPlayerChanges() {
	return playerChanges;
    }

    public TileChange[][] getTileChanges() {
	return tileChanges;
    }

}
