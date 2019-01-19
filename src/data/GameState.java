package data;

import java.util.LinkedHashSet;

import data.ai.Enemy;
import data.map.GameMap;
import data.player.Player;
import data.projectile.Projectile;

public class GameState {
	protected GameMap currentMap;
	protected LinkedHashSet<Enemy> enemies;
	protected LinkedHashSet<Projectile> projectiles;
	protected LinkedHashSet<Player> players;	

	public GameState() {
		// TODO Auto-generated constructor stub
	}
	
	

}
